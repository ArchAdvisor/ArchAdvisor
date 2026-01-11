import { http, HttpResponse } from "msw";

export const handlers = [
  http.get("/api/questionnaire-drafts/:draftId", ({ params }) => {
    return HttpResponse.json({
      id: params.draftId,
      projectName: "HDWD",
      architectureScope: "BACKEND_ONLY",
      deploymentPreference: "SERVERLESS",
      budgetTier: "MEDIUM",
      openSource: false,
      serverlessFriendly: true,
      expectedNumberOfUsers: 42,
      teamSize: 1,
      experienceLevel: "mixed",
      programmingLanguages: ["PYTHON"],
      priorityAspects: [
        "PERFORMANCE",
        "SCALABILITY",
        "MAINTAINABILITY",
        "SECURITY",
        "COST_EFFECTIVENESS",
        "COMMUNITY_SUPPORT",
        "ECOSYSTEM_MATURITY",
        "VENDOR_LOCKIN_AVOIDANCE",
      ],
      topRankN: 4,
    });
  }),

  // Create draft
  http.post("/api/questionnaire-drafts", async () => {
    return HttpResponse.json("new-draft-id-123");
  }),

  // Update draft
  http.put("/api/questionnaire-drafts/:draftId", async () => {
    return HttpResponse.json({ ok: true });
  }),

  // Submit questionnaire
  http.post("/api/questionnaire", async () => {
    return HttpResponse.json({
      architectureScope: "BACKEND_ONLY",
      backends: [
        {
          score: 1.23,
          technology: { id: 1, name: "Spring Boot", githubUrl: "", documentationUrl: "" },
          warnings: [],
        },
      ],
      frontends: null,
      databases: [
        {
          score: 0.9,
          technology: { id: 10, name: "PostgreSQL", githubUrl: "", documentationUrl: "" },
          warnings: [],
        },
      ],
      mobileFrameworks: null,
    });
  }),

  // PDF export
  http.post("/api/stack/pdf", async () => {
    const bytes = new Uint8Array([0x25, 0x50, 0x44, 0x46]);
    return new HttpResponse(bytes, {
      headers: {
        "Content-Type": "application/pdf",
        "Content-Disposition": 'attachment; filename="archadvisor-stack.pdf"',
      },
    });
  }),
];
