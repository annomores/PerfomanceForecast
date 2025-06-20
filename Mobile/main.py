from fastapi import FastAPI, File, UploadFile, Form, Depends, HTTPException, Request
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from mongoDB import DataBase
from machine_learning_service import PreprocessingData, ModelMachineLearning, Statistics
from auth import create_access_token, get_current_user
import shutil
import os
import pandas as pd

app = FastAPI()
db = DataBase()
preprocessor = PreprocessingData()
ml_model = ModelMachineLearning()
statistics = Statistics()

# Разрешенные источники
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

# ----------------------- Загрузка начальных данных ----------------------
@app.post("/upload_initial_data")
async def upload_initial_data(file: UploadFile = File(...)):
    file_path = os.path.join(UPLOAD_DIR, file.filename or "uploaded_file.xlsx")
    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)
    preprocessor.addPersonalInfo(file_path)
    return {"message": "Initial student data uploaded successfully."}

# ------------------- Загрузка данных об успеваемости -------------------
@app.post("/upload_study_data")
async def upload_study_data(subject_name: str = Form(...), file: UploadFile = File(...)):
    file_path = os.path.join(UPLOAD_DIR, file.filename or "uploaded_file.xlsx")
    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)
    preprocessor.addStudyData(file_path, subject_name)
    return {"message": "Study data uploaded successfully."}

# ----------------------------- Авторизация ------------------------------
@app.post("/auth")
async def auth_user(credentials: dict):
    role = credentials.get("role", "student")
    user = db.authentication(credentials["login"], credentials["password"], role=role)
    if not user:
        raise HTTPException(status_code=401, detail="Invalid credentials")
    token = create_access_token(data={"user_id": user["_id"], "role": role})
    return {"access_token": token, "role": role}

# ----------------------------- Получение данных -------------------------
@app.get("/get_personal_info")
async def get_personal_info(user=Depends(get_current_user)):
    return db.getUserPersonalInfo(user["_id"])

@app.get("/get_study_info")
async def get_study_info(subject: str, user=Depends(get_current_user)):
    student = db.getUserByID(user["_id"])
    # student - dict, преобразуем в DataFrame
    student_df = pd.DataFrame([student])
    return statistics.getStudyData(student_df)

@app.get("/get_statistics_by_group")
async def get_statistics_by_group(subject: str, user=Depends(get_current_user)):
    student = db.getUserByID(user["_id"])
    group = student["group"] if student and "group" in student else None
    students = db.getUsers(group)
    df = pd.DataFrame(students)
    df_grouped, intervals = statistics.splitOnPointsGroup(df, field="total_score", count_line=5)
    user_df = pd.DataFrame([student])
    user_score = statistics.getStudyData(user_df)["total_score"]
    interval_idx = statistics.findInterval(user_score, intervals)
    return {
        "intervals": intervals,
        "counts": df_grouped,
        "user_interval": interval_idx
    }

# ----------------------------- Получение auth ---------------------------
@app.post("/get_user_auth")
async def get_user_auth(data: dict):
    fio, group = data.get("fio"), data.get("group")
    user = db.findStudent(fio, group)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return {"login": user["login"], "password": user["password"]}

@app.put("/update_user_auth")
async def update_user_auth(data: dict):
    db.updateAuth(
        fio=data["fio"],
        group=data["group"],
        login=data["login"],
        password=data["password"]
    )
    return {"message": "User auth updated successfully"}

# -------------------------- Модель обучения -----------------------------
@app.get("/fit_model")
async def fit_model(subject: str):
    all_students = db.getUsers(None)
    df = pd.DataFrame(all_students)
    df = ml_model.prepareData(df, subject, allow_normilize=True)
    ml_model.prepareModel(df, subject)
    return {"message": "Model trained"}

@app.get("/predict")
async def predict(subject: str, group: str = "", user=Depends(get_current_user)):
    if group:
        students = db.getUsers(group)
    else:
        students = [db.getUserByID(user["_id"])]
    df = pd.DataFrame(students)
    df = ml_model.prepareData(df, subject, allow_normilize=True)
    predictions = ml_model.predictLabel(df, subject)
    return {"predictions": predictions}
