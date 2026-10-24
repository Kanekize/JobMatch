# JobMatch

## Local Ollama job matching

JobMatch can compare a job seeker's latest assessment with active jobs using a local Ollama model. No hosted AI API key is required. Requests are sent to the Ollama service on your own computer at `http://localhost:11434` by default.

### Requirements

- Java 25 or a JDK compatible with the existing Maven configuration
- Maven
- Ollama for Windows
- About 2 GB of disk space for the default `qwen2.5:3b` model, plus room for runtime memory use

### 1. Install and prepare Ollama

1. Install Ollama from the official Windows download page: https://ollama.com/download/windows
2. Open PowerShell and download the default model:

   ```powershell
   ollama pull qwen2.5:3b
   ```

3. Check that the model is available:

   ```powershell
   ollama list
   ```

Ollama normally runs its local API in the background. The default JobMatch settings use `http://localhost:11434` and model `qwen2.5:3b`.

### 2. Build JobMatch

Open PowerShell in the folder that contains this `pom.xml`, then run:

```powershell
mvn clean package
```

Maven will download the Jackson JSON dependency used to build and parse Ollama API requests. No Ollama cloud key or paid API is needed.

Open the project in your IDE and run `main.Main` using your installed JDK.

### 3. Try the matching workflow

1. Sign in as a job seeker.
2. Complete the JobMatch Assessment at least once.
3. Open **Recommended Jobs**.
4. Keep Ollama running while the page calculates match scores.
5. Hover over a match percentage to read the reason, matched skills, and job skills not evidenced by the assessment.

The **Best Match** sort uses the available match scores. If Ollama is not running or the model can't return usable results, JobMatch displays a clearly identified basic keyword estimate instead of pretending an AI result was generated. Start Ollama and reopen the job page to request AI results.

### Optional configuration

Set these environment variables before launching JobMatch if you want to use a different local model or API address:

```powershell
$env:JOBMATCH_OLLAMA_MODEL = "qwen2.5:3b"
$env:OLLAMA_BASE_URL = "http://localhost:11434"
```

The variables apply to programs started from that PowerShell session.

### Privacy and limitations

- Assessment matching is sent to the local Ollama API, not a hosted AI API.
- The integration omits profile type, resume filename, and profile/status or sensitive support/accommodation fields from the model input. Job-match scores should not be based on protected personal traits.
- Match percentages are estimates for job-seeker guidance, not a hiring probability or automated hiring decision. Results depend on how complete and accurate the assessment and job post are.
- The keyword estimate is a fallback, not an AI result.
- Review the model's license terms before redistributing Ollama models with the application.
