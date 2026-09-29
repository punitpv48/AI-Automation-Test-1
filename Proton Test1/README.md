# Proton Test1

A Java 17+ Selenium/TestNG project copied from the Proton automation framework. The dedicated email flow opens Proton sign-in, enters the configured credentials, checks “Keep me signed in”, signs in, composes the requested test message, and sends it. Chrome is visible by default (`headless=false`). Selenium Manager resolves browser drivers automatically.

## Project layout

- `src/main/java`: shared configuration, browser driver management, page objects, and API client.
- `src/test/java`: base fixtures, data providers, report listener, and UI/API tests.
- `src/test/resources/suites`: independent regression and smoke TestNG suites.
- `test-output/proton-email-test-results.xlsx`: generated Excel execution ledger with scenario steps, expected/actual results, and status.
- `target/extent-report.html`: per-run HTML report with test statuses, failure details, screenshots, and pass percentage.
- `target/screenshots`: screenshots captured for failed browser tests.

## Prerequisites

- JDK 17 or newer.
- Maven 3.9+ (`mvn -version`).
- Chrome installed for the default browser, or Firefox/Edge when selected.
- Network access to download Maven dependencies and Selenium-managed drivers.

## Run Proton email flow

Set credentials in the same PowerShell session that runs Maven. Do not put their values in source files, shell command arguments, or chat:

```powershell
$env:FRAMEWORK_PROTON_USERNAME = Read-Host "Proton test username"
$secret = Read-Host "Proton test password" -AsSecureString
$pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secret)
try {
	$env:FRAMEWORK_PROTON_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer)
} finally {
	[Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer)
}
mvn test -Dheadless=false
```

The test sends an email to `Ankit.verma@gmail.com` with subject `Test email` and body `Hi. Test`. Use only an account you control and an intended recipient. Proton may require MFA/CAPTCHA; this framework does not bypass those protections. The current public sign-in page did not expose a “Keep me signed in” checkbox during initial inspection, so the flow waits for that control and reports a clear failure if it is not present.

The XLSX ledger is written to `test-output/proton-email-test-results.xlsx` after each test attempt, including early failures. Secrets are never included in it.

## Run the copied demo suites

```powershell
mvn test -DsuiteXmlFile=src/test/resources/suites/smoke.xml
mvn test -DsuiteXmlFile=src/test/resources/suites/regression.xml
```

Both suites run methods in parallel (4 threads). To change the concurrency, edit `thread-count` and `data-provider-thread-count` in the selected suite XML. TestNG data providers also declare parallel execution.

Set execution properties from Maven when needed:

```powershell
mvn test -Dbrowser=firefox -Dheadless=false -DbaseUrl=https://www.saucedemo.com/ -DapiBaseUrl=https://jsonplaceholder.typicode.com -DuiApiUrl=https://jsonplaceholder.typicode.com/users/1
```

Supported browser values: `chrome`, `firefox`, and `edge`. Configuration defaults are in `src/test/resources/framework.properties`. Maven properties override the file values.

## Suite scope

- **Smoke**: basic SauceDemo login and page navigation, plus an API availability check that rejects server errors and empty responses.
- **Regression**: smoke coverage plus valid/invalid login data, inventory sorting and cart behavior, API status/content checks, and an API-to-browser-rendered-JSON comparison.

The examples use public demo services (SauceDemo and JSONPlaceholder). Replace the URLs, credentials, and locators with those for your application. The API/UI comparison demonstrates the mechanism by comparing a JSONPlaceholder API response with that same record rendered in a browser; adapt the page object and fields to compare your product's UI against its API.

## Reports

The TestNG listener writes `target/extent-report.html` after each suite run. It includes pass/fail/skip status, exception details, browser screenshots on UI failures, and a pass percentage. Reports are overwritten on each run; archive the file before another run if you need to retain history.

## Proton Mail UI checks

The Proton suites are separate from the SauceDemo/JSONPlaceholder examples:

```powershell
mvn test -DsuiteXmlFile=src/test/resources/suites/proton-smoke.xml
mvn test -DsuiteXmlFile=src/test/resources/suites/proton-regression.xml
```

The smoke test signs in and checks that the mailbox route opens. The regression suite includes that smoke check and a non-authenticated sign-in-form check. Proton tests run sequentially because they use an external account and repeated parallel sign-ins can trigger account security controls.

### GitHub Actions setup

The `Proton Mail UI Tests` workflow is started manually from the repository's **Actions** tab and offers `smoke` or `regression`. It uses the `proton-mail` GitHub Actions environment. Configure that environment under **Settings > Environments** and add:

- Environment secrets `PROTON_USERNAME` and `PROTON_PASSWORD` for a dedicated test account you control.
- Environment variable `PROTON_BASE_URL` with `https://mail.proton.me/`.
- Optionally require an environment reviewer and restrict deployment branches to the protected default branch.

The workflow maps these to `FRAMEWORK_PROTON_USERNAME`, `FRAMEWORK_PROTON_PASSWORD`, and `FRAMEWORK_PROTON_BASE_URL`; `Config` reads those environment values. Do not commit or paste credentials into source control, issues, chat, workflow inputs, or command-line arguments. Enter them directly in GitHub's secret-value form. Only trusted users should be able to edit workflows or dispatch the job. The workflow is manual-only and does not run on pull requests from forks.

Use a dedicated Proton test account with no private mailbox content. This password-only check does not handle MFA, CAPTCHA, or account recovery prompts and does not attempt to bypass them. Do not disable protections on a personal account to make automation pass. If the account requires an additional factor, the current smoke test will time out at the sign-in flow and needs a separately reviewed authentication strategy. GitHub artifacts include test reports but intentionally exclude browser screenshots, which could expose mailbox content.
