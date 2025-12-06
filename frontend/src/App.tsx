import QuestionnaireForm from './QuestionnaireForm';
import { Routes, Route } from "react-router-dom";
import ResultsPage from './ResultsPage';

function App() {
  return (
    <Routes>
      <Route path="/" element={<QuestionnaireForm />} />
      <Route path="/results" element={<ResultsPage />} />
    </Routes>
  );
}

export default App;
