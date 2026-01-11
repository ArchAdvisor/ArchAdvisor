export type Technology = {
  id: number;
  name: string;
  license: string;
  tags: string[];
  programmingLanguage: string;
  githubUrl: string;
  documentationUrl: string;
};

export type Recommendation = {
  technology: Technology;
  score: number;
  warnings: string[];
};

export type QuestionnaireResponse = {
  architectureScope: string;
  backends: Recommendation[] | null;
  frontends: Recommendation[] | null;
  databases: Recommendation[] | null;
  mobileFrameworks: Recommendation[] | null;
};
