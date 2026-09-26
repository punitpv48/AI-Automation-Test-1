# AI Automation Test 1

A Java 17+ test automation starter for browser UI and REST APIs. It uses Selenium WebDriver, TestNG, Maven, the Page Object Model, TestNG data providers, and Rest Assured. Selenium Manager resolves browser drivers automatically.

## Project layout

- `src/main/java`: shared configuration, browser driver management, page objects, and API client.
- `src/test/java`: base fixtures, data providers, report listener, and UI/API tests.
- `src/test/resources/suites`: independent regression and smoke TestNG suites.
- `target/extent-report.html`: per-run HTML report with test statuses, failure details, screenshots, and pass percentage.
- `target/screenshots`: screenshots captured for failed browser tests.

## Prerequisites

- JDK 17 or newer.
- Maven 3.9+ (`mvn -version`).
- Chrome installed for the default browser, or Firefox/Edge when selected.
- Network access to download Maven dependencies and Selenium-managed drivers.

## Run the suites

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
