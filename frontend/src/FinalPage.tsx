import { useLocation, useNavigate } from "react-router-dom";
import type { QuestionnaireResponse, Recommendation } from "./types/QuestionnaireResponse";
import type { ArchitectureScope } from "./QuestionnaireForm";

type PersonalStack = {
    backend: Recommendation | null;
    frontend: Recommendation | null;
    database: Recommendation | null;
    mobile: Recommendation | null;
};

type LocationState = {
    result: QuestionnaireResponse;
    personalStack: PersonalStack;
};

function FinalStackPage() {
    const navigate = useNavigate();
    const location = useLocation();
    const state = location.state as LocationState | null;

    if (!state) {
        return (
            <div style={{ maxWidth: 800, margin: "2rem auto", fontFamily: "sans-serif" }}>
                <h1>Final Stack</h1>
                <p>No stack data available. Please fill out the questionnaire again.</p>
                <button onClick={() => navigate("/")}>Back to questionnaire</button>
            </div>
        );
    }

    const { result, personalStack } = state;

    const renderItem = (
        label: string,
        rec: Recommendation | null,
        architectureScope: string,
        visibleForScopes: string[]
    ) => {
        if (!visibleForScopes.includes(architectureScope)) {
            return null;
        }

        return (
            <li>
                <strong>{label}:</strong>{" "}
                {rec ? rec.technology.name : "Not selected"}
            </li>
        );
    };

    return (
        <div style={{ maxWidth: 800, margin: "2rem auto", fontFamily: "sans-serif" }}>
            <h1>Your final stack</h1>
            <p>
                <strong>Scope:</strong> {result.architectureScope}
            </p>

            <ul>
                {renderItem("Backend", personalStack.backend, result.architectureScope, [
                    "BACKEND_ONLY",
                    "FULL_STACK"
                ])}

                {renderItem("Frontend", personalStack.frontend, result.architectureScope, [
                    "FULL_STACK",
                ])}

                {renderItem("Database", personalStack.database, result.architectureScope, [
                    "FULL_STACK",
                ])}

                {renderItem("Mobile", personalStack.mobile, result.architectureScope, [
                    "MOBILE",
                ])}
            </ul>



            <button style={{ marginTop: "2rem" }} onClick={() => navigate("/")}>
                Start over
            </button>
        </div>
    );
}

export default FinalStackPage;
