import { useLocation, useNavigate } from "react-router-dom";
import type { QuestionnaireResponse } from "./types/QuestionnaireResponse";

type LocationState = {
  result: QuestionnaireResponse;
};

function ResultsPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const state = location.state as LocationState | null;

  if (!state || !state.result) {
    return (
      <div style={{ maxWidth: 800, margin: "2rem auto", fontFamily: "sans-serif" }}>
        <h1>Results</h1>
        <p>No results available. Please fill out the questionnaire first.</p>
        <button onClick={() => navigate("/")}>Back to questionnaire</button>
      </div>
    );
  }

  const { result } = state;

  const renderSection = (title: string, items: QuestionnaireResponse["backends"]) => {
    console.log("Rendering section:", items);
    if (!items || items.length === 0) return null;
    return (
      <section style={{ marginTop: "1.5rem" }}>
        <h2>{title}</h2>

        <ul>
          {items.map((rec, idx) => (
            <li key={rec.technology.id ?? idx} style={{ marginBottom: "0.75rem" }}>
              <div>
                <strong>{rec.technology.name}</strong> – total score {rec.score.toFixed(2)}
              </div>
              <div style={{ marginTop: "0.25rem", fontSize: "0.9rem" }}>
                {rec.technology.githubUrl && (
                  <a
                    href={rec.technology.githubUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    style={{ marginRight: "0.75rem" }}
                  >
                    GitHub
                  </a>
                )}
                {rec.technology.documentationUrl && (
                  <a
                    href={rec.technology.documentationUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    Documentation
                  </a>
                )}
              </div>

              {rec.warnings && rec.warnings.length > 0 && (
                <ul style={{ marginTop: "0.25rem", marginLeft: "1.5rem" }}>
                  {rec.warnings.map((w, i) => (
                    <li key={i}>{w}</li>
                  ))}
                </ul>
              )}
            </li>
          ))}
        </ul>
      </section>
    );
  };

  return (
    <div style={{ maxWidth: 800, margin: "2rem auto", fontFamily: "sans-serif" }}>
      <h1>Recommended Architecture</h1>
      <p>
        <strong>Scope:</strong> {result.architectureScope}
      </p>

      {/* BACKEND_ONLY */}
      {result.architectureScope === "BACKEND_ONLY" && (
        <>
          {renderSection("Backend frameworks", result.backends)}
          {renderSection("Databases", result.databases)}
        </>
      )}
      {/* FULL_STACK */}
      {result.architectureScope === "FULL_STACK" && (
        <>
          {renderSection("Backend frameworks", result.backends)}
          {renderSection("Frontend frameworks", result.frontends)}
          {renderSection("Databases", result.databases)}
        </>
      )}
      {/* MOBILE */}
      {result.architectureScope === "MOBILE" && (
        <>
          {renderSection("Mobile frameworks", result.mobileFrameworks)}
          {renderSection("Backend frameworks", result.backends)}
          {renderSection("Databases", result.databases)}
        </>
      )}
      <button style={{ marginTop: "2rem" }} onClick={() => navigate("/")}>
        Start over
      </button>
    </div>
  );
}

export default ResultsPage;
