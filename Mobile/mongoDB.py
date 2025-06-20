from pymongo import MongoClient, UpdateOne
from bson.objectid import ObjectId
import hashlib

class DataBase:
    def __init__(self, uri="mongodb://localhost:27017", db_name="university"):
        self.client = MongoClient(uri)
        self.db = self.client[db_name]
        self.students = self.db["students"]
        self.teachers = self.db["teachers"]
        self.users = self.db["users"]

    # -------------------- Поиск студента --------------------
    def findStudent(self, fio, group):
        return self.students.find_one({"fio": fio, "group": group})

    # -------------------- Поиск преподавателя --------------------
    def findTeacher(self, fio):
        return self.teachers.find_one({"fio": fio})

    # -------------------- Вставка/обновление студента --------------------
    def insertStudent(self, student_json):
        fio = student_json.get("fio")
        group = student_json.get("group")
        existing = self.findStudent(fio, group)

        if existing:
            self.students.update_one(
                {"_id": existing["_id"]},
                {"$set": student_json}
            )
        else:
            self.students.insert_one(student_json)

    # -------------------- Вставка/обновление преподавателя --------------------
    def insertTeacher(self, teacher_json):
        fio = teacher_json.get("fio")
        existing = self.findTeacher(fio)
        if existing:
            self.teachers.update_one(
                {"_id": existing["_id"]},
                {"$set": teacher_json}
            )
        else:
            self.teachers.insert_one(teacher_json)

    # -------------------- Добавление предмета студенту --------------------
    def insertSubjectInStudent(self, subject_name, list_columns, subject_data):
        fio = subject_data.get("fio")
        group = subject_data.get("group")
        student = self.findStudent(fio, group)

        if student:
            update_fields = {f"subjects.{subject_name}.{col}": subject_data[col] for col in list_columns}
            self.students.update_one(
                {"_id": student["_id"]},
                {"$set": update_fields}
            )
        else:
            raise ValueError("Student not found")

    # -------------------- Авторизация --------------------
    def authentication(self, login, password, role="student"):
        hashed_password = self._hash_password(password)
        if role == "student":
            return self.students.find_one({"login": login, "password": hashed_password})
        elif role == "teacher":
            return self.teachers.find_one({"login": login, "password": hashed_password})
        else:
            return None

    # -------------------- Получение пользователя --------------------
    def getUserByID(self, id, role="student"):
        if role == "student":
            return self.students.find_one({"_id": ObjectId(id)})
        elif role == "teacher":
            return self.teachers.find_one({"_id": ObjectId(id)})
        return None

    def getUserPersonalInfo(self, id, role="student"):
        user = self.getUserByID(id, role)
        if not user:
            return None
        return {
            "fio": user.get("fio"),
            "group": user.get("group"),
            "gender": user.get("gender"),
            "type_study": user.get("type_study"),
            "ponts_USE": user.get("points_USE")
        }

    # -------------------- Получение студентов группы --------------------
    def getUsers(self, group):
        if group:
            return list(self.students.find({"group": group}))
        return list(self.students.find({}))

    # -------------------- Обновление логина и пароля --------------------
    def updateAuth(self, fio, group, login, password, role="student"):
        if role == "student":
            student = self.findStudent(fio, group)
            if student:
                hashed_password = self._hash_password(password)
                self.students.update_one(
                    {"_id": student["_id"]},
                    {"$set": {"login": login, "password": hashed_password}}
                )
            else:
                raise ValueError("Student not found")
        elif role == "teacher":
            teacher = self.findTeacher(fio)
            if teacher:
                hashed_password = self._hash_password(password)
                self.teachers.update_one(
                    {"_id": teacher["_id"]},
                    {"$set": {"login": login, "password": hashed_password}}
                )
            else:
                raise ValueError("Teacher not found")

    # -------------------- Хэширование пароля --------------------
    def _hash_password(self, password):
        return hashlib.sha256(password.encode()).hexdigest()
