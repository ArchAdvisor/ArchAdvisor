import QuestionnaireForm from './QuestionnaireForm';
import { Routes, Route } from "react-router-dom";
import ResultsPage from './ResultsPage';
import FinalStackPage from './FinalPage';

function App() {
  return (
    <Routes>
      <Route path="/" element={<QuestionnaireForm />} />
      <Route path="/results" element={<ResultsPage />} />
      <Route path="/final" element={<FinalStackPage />} />
    </Routes>
  );
}

export default App;
