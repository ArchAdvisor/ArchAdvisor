import QuestionnaireForm from './QuestionnaireForm';
import { Routes, Route, Navigate } from "react-router-dom";
import ResultsPage from './ResultsPage';
import FinalStackPage from './FinalPage';
import { AppShell } from "./AppShell";

function App() {
  return (
    <AppShell>
      <Routes>
        <Route path="/" element={<QuestionnaireForm />} />
        <Route path="/results" element={<ResultsPage />} />
        <Route path="/final" element={<FinalStackPage />} />
        <Route path="/draft/:draftId">
          <Route index element={<Navigate to="latest" replace />} />
          <Route path="latest" element={<QuestionnaireForm />} />
          <Route path=":version" element={<QuestionnaireForm />} />
        </Route>
      </Routes>
    </AppShell>
  );
}

export default App;
