# Trivia Bridge

Trivia Bridge is a full-stack quiz application powered by the [Open Trivia Database](https://opentdb.com/). Users can configure a quiz, answer its questions, and review their score and correct answers.

The Quarkus backend acts as a bridge between the React frontend and Open Trivia DB. It keeps correct answers on the server until the quiz is submitted, preventing them from being revealed in the initial API response.

## Project context

This project was created as a solution to the [Quad programming assignment](https://www.quad.team/assignment).
The implementation and technical decisions in this repository are my submission.

## Features

- Generate quizzes with 1 to 50 questions
- Filter by category, difficulty, and question type
- Answer multiple-choice and true-or-false questions
- Validate and score complete quiz submissions on the backend
- Review selected and correct answers after submission
- Handle invalid input and upstream provider errors
- Automatically expire quizzes from an in-memory cache

## How it works

1. The frontend requests a quiz from the backend.
2. The backend retrieves questions from Open Trivia DB.
3. Question and answer IDs are generated locally, while the correct answers are stored temporarily in a Caffeine cache.
4. The frontend receives shuffled answer options without information about correctness.
5. After the user submits exactly one answer per question, the backend validates and scores the quiz.

## Technology

### Backend

- Java 21
- Quarkus
- Maven
- RESTEasy Reactive
- Caffeine Cache

### Frontend

- React 19
- TypeScript
- Vite
- Tailwind CSS and shadcn/ui
- Axios and Zod

## Getting started

### Docker Compose

The quickest way to run the complete application is with [Docker](https://www.docker.com/) and Docker Compose.

Copy the example configuration and start both containers:

```bash
cp .env.example .env
docker compose up --build
```

On Windows PowerShell, use `Copy-Item .env.example .env` instead of `cp`.

Open <http://localhost:5173> in your browser. The backend API is available at <http://localhost:8080>.

Stop the application with:

```bash
docker compose down
```

### Local development

For development with hot reload, install Java 21, Node.js 22.12 or newer, and npm.

Start the backend from the repository root:

```bash
cd trivia-bridge
./mvnw quarkus:dev
```

On Windows, replace `./mvnw` with `.\mvnw.cmd`.

Start the frontend in another terminal:

```bash
cd trivia-fe
npm install
npm run dev
```

In local development mode, Swagger UI is available at <http://localhost:8080/q/swagger-ui>.

## Configuration

| Environment variable | Default | Description |
| --- | --- | --- |
| `VITE_API_BASE_URL` | `http://localhost:8080` | Backend URL used by the frontend |
| `QUARKUS_HTTP_CORS_ORIGINS` | `http://localhost:5173` | Origins allowed to call the backend |
| `QUIZ_CACHE_TTL` | `1h` | How long generated quizzes remain available |
| `QUIZ_CACHE_MAXIMUM_SIZE` | `10000` | Maximum number of cached quizzes |

Docker Compose and Vite both read these values from the root `.env` file. Vite only exposes variables prefixed with `VITE_` to browser code. `VITE_API_BASE_URL` is included in the frontend bundle when its image is built, so rebuild the image after changing it.

When running the backend without Docker, Quarkus does not automatically load the root `.env`; provide its variables through your shell or IDE run configuration. All variables have development defaults, so no configuration is required for the standard local ports.

## API

### Create a quiz

```http
GET /questions?amount=10&category=0&difficulty=ANY&type=ANY
```

| Parameter | Accepted values |
| --- | --- |
| `amount` | An integer from `1` to `50` |
| `category` | An Open Trivia DB category ID, or `0` for any category |
| `difficulty` | `ANY`, `EASY`, `MEDIUM`, or `HARD` |
| `type` | `ANY`, `MULTIPLE`, or `BOOLEAN` |

The response contains a quiz ID and shuffled answer options. It does not identify the correct answers.

### Check answers

```http
POST /checkanswers
Content-Type: application/json
```

Example request:

```json
{
  "quizId": "8bc8f95e-8ee5-43ce-9513-97e7ed68b84d",
  "answers": [
    {
      "questionId": "251fc70d-62bc-4300-9eff-e8613a169e64",
      "answerId": "25d44c71-c82a-403d-a5e7-2803e1912417"
    }
  ]
}
```

Exactly one answer must be submitted for every question in the quiz. The response includes the score and a result for each question.

## Current limitations

- Quizzes are stored in memory and are lost when the backend restarts.
- The cache is local to one backend instance and is not shared between instances.
- Quiz generation depends on the availability of Open Trivia DB.
