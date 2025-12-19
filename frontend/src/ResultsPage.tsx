import { useLocation, useNavigate } from "react-router-dom";
import type { QuestionnaireResponse, Recommendation } from "./types/QuestionnaireResponse";
import { useState } from "react";
import type { PersonalStack } from "./types/PersonalStack";

type LocationState = {
  result: QuestionnaireResponse;
  draftLink?: string | null;
  draftId?: string | null;
};

function ResultsPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const state = location.state as LocationState | null;
  const draftLink = state?.draftLink;
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
  const [personalStack, setPersonalStack] = useState<PersonalStack>({ backend: null, frontend: null, database: null, mobile: null });
  const handleAddToStack = (category: string, rec: Recommendation) => {
    setPersonalStack(prev => ({
      ...prev,
      [category]: rec,
    }));
  }
  const [warning, setWarning] = useState<string | null>(null);
  const handleGoToFinal = () => {
    const missing: string[] = [];

    switch (result.architectureScope) {
      case "BACKEND_ONLY":
        if (!personalStack.backend) missing.push("Backend");
        break;

      case "FULL_STACK":
        if (!personalStack.backend) missing.push("Backend");
        if (!personalStack.frontend) missing.push("Frontend");
        if (!personalStack.database) missing.push("Database");
        break;

      case "MOBILE":
        if (!personalStack.mobile) missing.push("Mobile");
        break;
    }

    if (missing.length > 0) {
      setWarning(`Please add: ${missing.join(", ")}`);
      return;
    }

    setWarning(null);
    navigate("/final", {
      state: {
        result,
        personalStack,
        draftLink,
        draftId: state.draftId,
      },
    });
  };

  const renderSection = (title: string, items: QuestionnaireResponse["backends"], category: string, personalStack: PersonalStack, onAdd: (categrory: string, rec: Recommendation) => void) => {
    //console.log("Rendering section:", items);
    if (!items || items.length === 0) return null;
    return (
      <section style={{ marginTop: "1.5rem" }}>
        <h2>{title}</h2>
        <ul>
          {items.map((rec, idx) => {
            const isSelected =
              personalStack[category]?.technology.id === rec.technology.id;

            return (
              <li
                key={rec.technology.id ?? idx}
                style={{
                  marginBottom: "0.75rem",
                  padding: "0.5rem",
                  border: "1px solid #ddd",
                  borderRadius: "4px",
                  backgroundColor: isSelected ? "#f0f8ff" : "white",
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <div>
                  <strong>{rec.technology.name}</strong> – total score{" "}
                  {rec.score.toFixed(2)}
                  {rec.warnings && rec.warnings.length > 0 && (
                    <ul style={{ marginTop: "0.25rem", marginLeft: "1.5rem" }}>
                      {rec.warnings.map((w, i) => (
                        <li key={i}>{w}</li>
                      ))}
                    </ul>
                  )}

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
                </div>

                <button
                  type="button"
                  onClick={() => onAdd(category, rec)}
                  disabled={isSelected}
                  style={{ marginLeft: "1rem" }}
                >
                  {isSelected ? "Selected" : "+"}
                </button>
              </li>
            );
          })}
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
          {renderSection("Backend frameworks", result.backends, "backend", personalStack, handleAddToStack)}
          {renderSection("Databases", result.databases, "database", personalStack, handleAddToStack)}
        </>
      )}
      {/* FULL_STACK */}
      {result.architectureScope === "FULL_STACK" && (
        <>
          {renderSection("Backend frameworks", result.backends, "backend", personalStack, handleAddToStack)}
          {renderSection("Frontend frameworks", result.frontends, "frontend", personalStack, handleAddToStack)}
          {renderSection("Databases", result.databases, "database", personalStack, handleAddToStack)}
        </>
      )}
      {/* MOBILE */}
      {result.architectureScope === "MOBILE" && (
        <>
          {renderSection("Mobile frameworks", result.mobileFrameworks, "mobile", personalStack, handleAddToStack)}
        </>
      )}
      {/* Personal stack summary */}
      <section style={{ marginTop: "2rem", paddingTop: "1rem", borderTop: "1px solid #ccc" }}>
        <h2>Your selected stack</h2>
        <ul>
          {result.architectureScope !== "MOBILE" && (
            <li>
              <strong>Backend:</strong>{" "}
              {personalStack.backend ? personalStack.backend.technology.name : "None selected"}
            </li>
          )}
          {result.architectureScope === "FULL_STACK" && (
            <li>
              <strong>Frontend:</strong>{" "}
              {personalStack.frontend ? personalStack.frontend.technology.name : "None selected"}
            </li>
          )}
          {(result.architectureScope === "FULL_STACK") && (
            <li>
              <strong>Database:</strong>{" "}
              {personalStack.database ? personalStack.database.technology.name : "None selected"}
            </li>
          )}
          {result.architectureScope === "MOBILE" && (
            <li>
              <strong>Mobile:</strong>{" "}
              {personalStack.mobile ? personalStack.mobile.technology.name : "None selected"}
            </li>
          )}
        </ul>
      </section>
      {/* Warning message */}
      {warning && (
        <p style={{ color: "red", marginTop: "1rem" }}>
          {warning}
        </p>
      )}

      <button onClick={handleGoToFinal}>
        Continue to final stack
      </button>
      <button style={{ marginTop: "2rem" }} onClick={() => navigate("/")}>
        Start over
      </button>
    </div>
  );
}

export default ResultsPage;
