import QuestionnaireForm from './QuestionnaireForm';
import { Routes, Route } from "react-router-dom";
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
        <Route path="/draft/:draftId" element={<QuestionnaireForm />} />
      </Routes>
    </AppShell>
  );
}

export default App;
