import numpy as np
import pandas as pd
from mongoDB import DataBase
from sklearn.linear_model import LogisticRegression, LinearRegression
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, mean_squared_error

def read_student_data(file_path):
    df = pd.read_excel(file_path)
    return df

db = DataBase()

class PreprocessingData:
    def fillEmptyUSE(self, df):
        groups = set()
        for val in df["group"].unique().tolist():
            group = int(val)
            groups.add(group)
        groups = list(groups)
        for group in groups:
            mean_group = df[df['group'].astype(int) == int(group)]["USE"].mean()
            df.loc[(df['group'].astype(int) == int(group)) & (np.isnan(df["USE"])), "USE"] = int(mean_group)
        return df

    def normilizeData(self, df, columns):
        for col in columns:
            min_val = df[col].min()
            max_val = df[col].max()
            df[col] = df[col].apply(lambda x: (x - min_val) / (max_val - min_val) if max_val != min_val else 0)
        return df

    def addPersonalInfo(self, file_path):
        df = read_student_data(file_path)
        df = self.fillEmptyUSE(df)
        df = self.normilizeData(df, columns=[col for col in df.columns if col not in ['fio', 'group', 'object_name']])
        for record in df.to_dict(orient='records'):
            db.insertStudent(record)

    def addStudyData(self, file_path, subject_name):
        df = read_student_data(file_path)
        df = self.fillEmptyUSE(df)
        df = self.normilizeData(df, columns=[col for col in df.columns if col not in ['fio', 'group', 'object_name']])
        for record in df.to_dict(orient='records'):
            db.insertSubjectInStudent(subject_name, df.columns, record)

class ModelMachineLearning:
    def __init__(self):
        self.classifier = None
        self.regressor = None

    def prepareData(self, df, subject, allow_normilize=False):
        # Фильтрация по object_name
        df = df[df['object_name'] == subject]
        if allow_normilize:
            # Пример: нормализация всех числовых колонок кроме идентификаторов
            num_cols = df.select_dtypes(include=[np.number]).columns.tolist()
            for col in ['USE', 'score']:
                if col in num_cols:
                    min_val = df[col].min()
                    max_val = df[col].max()
                    df[col] = df[col].apply(lambda x: (x - min_val) / (max_val - min_val) if max_val != min_val else 0)
        return df

    def prepareModel(self, df, subject):
        if subject == 'ООП':
            # Классификация: пример с LogisticRegression
            X = df.drop(columns=['label', 'object_name', 'fio', 'group'], errors='ignore')
            y = df['label']  # label — целевая переменная для классификации
            X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
            self.classifier = LogisticRegression(max_iter=1000)
            self.classifier.fit(X_train, y_train)
            y_pred = self.classifier.predict(X_test)
            acc = accuracy_score(y_test, y_pred)
            print(f'Classification accuracy (ООП): {acc}')
        elif subject == 'Мат. анализ':
            # Регрессия: пример с LinearRegression
            X = df.drop(columns=['target', 'object_name', 'fio', 'group'], errors='ignore')
            y = df['target']  # target — целевая переменная для регрессии
            X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
            self.regressor = LinearRegression()
            self.regressor.fit(X_train, y_train)
            y_pred = self.regressor.predict(X_test)
            mse = mean_squared_error(y_test, y_pred)
            print(f'Regression MSE (Мат. анализ): {mse}')
        else:
            raise ValueError('Unknown subject for ML')

    def predictLabel(self, df, subject):
        if subject == 'ООП':
            if self.classifier is None:
                raise ValueError('Classifier not trained')
            X = df.drop(columns=['label', 'object_name', 'fio', 'group'], errors='ignore')
            return self.classifier.predict(X).tolist()
        elif subject == 'Мат. анализ':
            if self.regressor is None:
                raise ValueError('Regressor not trained')
            X = df.drop(columns=['target', 'object_name', 'fio', 'group'], errors='ignore')
            return self.regressor.predict(X).tolist()
        else:
            raise ValueError('Unknown subject for ML')

class Statistics:
    def getStudyData(self, user_df):
        total_score = user_df['score'].sum() if 'score' in user_df else 0
        missed_lectures = user_df['missed_lectures'].sum() if 'missed_lectures' in user_df else 0
        return {"total_score": total_score, "missed_lectures": missed_lectures}

    def sumValue(self, df, list_col):
        return df[list_col].sum()

    def splitOnPointsGroup(self, df, field, count_line):
        min_val, max_val = df[field].min(), df[field].max()
        intervals = np.linspace(min_val, max_val, count_line + 1)
        df['interval'] = pd.cut(df[field], bins=intervals, include_lowest=True, labels=False)
        counts = df['interval'].value_counts().sort_index().tolist()
        return counts, intervals.tolist()

    def findInterval(self, user_value, groups):
        for i in range(len(groups) - 1):
            if groups[i] <= user_value < groups[i + 1]:
                return i
        return len(groups) - 2
