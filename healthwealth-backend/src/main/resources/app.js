const API_BASE_URL = "http://localhost:8080/api";

const app = document.getElementById("app");

app.innerHTML = `
    <header class="navbar">
        <div class="logo">
            <span class="logo-icon">+</span>
            <span>HealthWealth</span>
        </div>

        <nav>
            <a href="#home">Home</a>
            <a href="#patients">Patients</a>
            <a href="#hospitals">Hospitals</a>
            <a href="#recommendations">Recommendations</a>
        </nav>
    </header>

    <main>

        <!-- HOME -->
        <section id="home" class="hero">
            <div class="hero-content">
                <span class="badge">Healthcare Navigation</span>

                <h1>
                    Find the Right
                    <span>Healthcare Option</span>
                </h1>

                <p>
                    HealthWealth helps compare hospitals, government schemes,
                    insurance coverage and estimated healthcare costs based
                    on a patient's profile.
                </p>

                <div class="hero-buttons">
                    <button class="btn primary" onclick="showSection('patients')">
                        Find Recommendations
                    </button>

                    <button class="btn secondary" onclick="showSection('hospitals')">
                        Explore Hospitals
                    </button>
                </div>
            </div>

            <div class="hero-card">
                <div class="health-icon">+</div>
                <h3>Healthcare Decision Support</h3>
                <p>
                    Transparent and explainable recommendations
                    based on available data.
                </p>
            </div>
        </section>


        <!-- PATIENT SEARCH -->
        <section id="patients" class="section">

            <div class="section-header">
                <span class="badge">Patient Profile</span>

                <h2>Find Healthcare Recommendations</h2>

                <p>
                    Enter a patient ID to retrieve their healthcare options.
                </p>
            </div>

            <div class="search-card">

                <label for="patientId">
                    Patient ID
                </label>

                <div class="input-row">
                    <input
                        type="number"
                        id="patientId"
                        placeholder="Example: 1"
                        min="1"
                    />

                    <button
                        class="btn primary"
                        onclick="loadRecommendations()"
                    >
                        Get Recommendations
                    </button>
                </div>

                <p id="patientMessage" class="message"></p>

            </div>

        </section>


        <!-- RECOMMENDATIONS -->
        <section id="recommendations" class="section">

            <div class="section-header">
                <span class="badge">Recommendation Engine</span>

                <h2>Healthcare Recommendations</h2>

                <p>
                    Recommended options based on patient profile,
                    hospital availability, insurance and schemes.
                </p>
            </div>

            <div id="recommendationResult">

                <div class="empty-state">
                    <div class="empty-icon">+</div>

                    <h3>No Recommendation Selected</h3>

                    <p>
                        Enter a patient ID above to see recommendations.
                    </p>
                </div>

            </div>

        </section>


        <!-- HOSPITALS -->
        <section id="hospitals" class="section">

            <div class="section-header">
                <span class="badge">Healthcare Providers</span>

                <h2>Hospitals</h2>

                <p>
                    Browse hospitals available for healthcare services.
                </p>
            </div>

            <div class="hospital-search">

                <input
                    type="text"
                    id="cityInput"
                    placeholder="Enter city e.g. Pune"
                />

                <input
                    type="number"
                    id="diseaseInput"
                    placeholder="Disease ID (optional)"
                    min="1"
                />

                <button
                    class="btn primary"
                    onclick="loadHospitals()"
                >
                    Search Hospitals
                </button>

            </div>

            <div id="hospitalResult"></div>

        </section>

    </main>


    <footer>
        <div>
            <strong>HealthWealth</strong>
            <p>
                Healthcare navigation prototype.
            </p>
        </div>

        <div>
            <p>
                Local development prototype
            </p>
        </div>
    </footer>
`;


/* =========================================================
   SECTION NAVIGATION
   ========================================================= */

function showSection(sectionId) {

    const section = document.getElementById(sectionId);

    if (section) {
        section.scrollIntoView({
            behavior: "smooth"
        });
    }
}


/* =========================================================
   LOAD PATIENT RECOMMENDATIONS
   ========================================================= */

async function loadRecommendations() {

    const patientId =
        document.getElementById("patientId").value;

    const message =
        document.getElementById("patientMessage");

    const result =
        document.getElementById("recommendationResult");


    if (!patientId || patientId < 1) {

        message.textContent =
            "Please enter a valid patient ID.";

        message.className =
            "message error";

        return;
    }


    message.textContent =
        "Loading recommendations...";

    message.className =
        "message";


    result.innerHTML = `
        <div class="loading">
            Loading healthcare recommendations...
        </div>
    `;


    try {

        const response = await fetch(
            `${API_BASE_URL}/recommendations/${patientId}`
        );


        if (!response.ok) {

            let errorMessage =
                "Unable to load recommendations.";

            try {
                const errorData = await response.json();

                if (errorData.message) {
                    errorMessage = errorData.message;
                }

            } catch (e) {
                // Ignore invalid error response
            }

            throw new Error(errorMessage);
        }


        const data = await response.json();

        displayRecommendations(data);


        message.textContent =
            "Recommendations loaded successfully.";

        message.className =
            "message success";


        showSection("recommendations");

    } catch (error) {

        console.error(error);

        message.textContent =
            error.message;

        message.className =
            "message error";


        result.innerHTML = `
            <div class="error-state">

                <h3>Unable to Load Recommendations</h3>

                <p>
                    ${escapeHtml(error.message)}
                </p>

                <p>
                    Make sure the Spring Boot backend is running
                    on port 8080.
                </p>

            </div>
        `;
    }
}


/* =========================================================
   DISPLAY RECOMMENDATIONS
   ========================================================= */

function displayRecommendations(data) {

    const result =
        document.getElementById("recommendationResult");


    if (!data) {

        result.innerHTML = `
            <div class="empty-state">
                <h3>No Data Found</h3>
            </div>
        `;

        return;
    }


    const patientId =
        data.patientId ?? data.getPatientId;

    const patientName =
        data.patientName ?? data.getPatientName;

    const diseaseId =
        data.diseaseId ?? data.getDiseaseId;

    const diseaseName =
        data.diseaseName ?? data.getDiseaseName;

    const options =
        data.options ?? [];


    let optionsHtml = "";


    if (options.length === 0) {

        optionsHtml = `
            <div class="empty-state">

                <h3>No Healthcare Options Found</h3>

                <p>
                    No matching hospital or healthcare option
                    was found for this patient.
                </p>

            </div>
        `;

    } else {

        optionsHtml = options
            .map((option, index) => createRecommendationCard(option, index))
            .join("");
    }


    result.innerHTML = `

        <div class="patient-summary">

            <div>
                <span>Patient</span>
                <strong>
                    ${escapeHtml(patientName ?? "Unknown")}
                </strong>
            </div>

            <div>
                <span>Patient ID</span>
                <strong>
                    ${patientId ?? "-"}
                </strong>
            </div>

            <div>
                <span>Disease</span>
                <strong>
                    ${escapeHtml(diseaseName ?? "Not specified")}
                </strong>
            </div>

            <div>
                <span>Disease ID</span>
                <strong>
                    ${diseaseId ?? "-"}
                </strong>
            </div>

        </div>


        <div class="recommendation-list">

            ${optionsHtml}

        </div>
    `;
}


/* =========================================================
   RECOMMENDATION CARD
   ========================================================= */

function createRecommendationCard(option, index) {

    const hospitalName =
        option.hospitalName ??
        option.name ??
        "Hospital";

    const city =
        option.city ??
        "";

    const rating =
        option.rating ??
        "-";

    const estimatedCost =
        option.estimatedCost ??
        option.cost ??
        null;

    const availableSlots =
        option.availableSlots ??
        option.slots ??
        0;

    const emergencyAvailable =
        option.emergencyAvailable ??
        false;

    const score =
        option.score ??
        null;

    const explanation =
        option.explanation ??
        option.reason ??
        "Recommended based on available healthcare information.";


    return `

        <div class="recommendation-card">

            <div class="recommendation-rank">
                #${index + 1}
            </div>


            <div class="recommendation-content">

                <div class="recommendation-header">

                    <div>

                        <h3>
                            ${escapeHtml(hospitalName)}
                        </h3>

                        <p class="hospital-city">
                            ${escapeHtml(city)}
                        </p>

                    </div>

                    ${
                        score !== null
                            ? `<span class="score">
                                Score: ${score}
                               </span>`
                            : ""
                    }

                </div>


                <div class="recommendation-details">

                    <div>
                        <span>Rating</span>
                        <strong>
                            ${rating}
                        </strong>
                    </div>

                    <div>
                        <span>Available Slots</span>
                        <strong>
                            ${availableSlots}
                        </strong>
                    </div>

                    <div>
                        <span>Estimated Cost</span>
                        <strong>
                            ${
                                estimatedCost !== null
                                    ? "₹" + formatNumber(estimatedCost)
                                    : "Not available"
                            }
                        </strong>
                    </div>

                    <div>
                        <span>Emergency</span>
                        <strong>
                            ${emergencyAvailable ? "Available" : "Not Available"}
                        </strong>
                    </div>

                </div>


                <div class="recommendation-explanation">

                    <strong>
                        Why this option?
                    </strong>

                    <p>
                        ${escapeHtml(explanation)}
                    </p>

                </div>

            </div>

        </div>
    `;
}


/* =========================================================
   LOAD HOSPITALS
   ========================================================= */

async function loadHospitals() {

    const city =
        document.getElementById("cityInput").value.trim();

    const diseaseId =
        document.getElementById("diseaseInput").value;


    const result =
        document.getElementById("hospitalResult");


    result.innerHTML = `
        <div class="loading">
            Loading hospitals...
        </div>
    `;


    try {

        const params = new URLSearchParams();

        params.append("page", "0");
        params.append("size", "20");


        if (city) {
            params.append("city", city);
        }


        if (diseaseId) {
            params.append("diseaseId", diseaseId);
        }


        const response = await fetch(
            `${API_BASE_URL}/hospitals?${params.toString()}`
        );


        if (!response.ok) {

            let message =
                "Unable to load hospitals.";

            try {

                const errorData =
                    await response.json();

                if (errorData.message) {
                    message = errorData.message;
                }

            } catch (e) {
                // Ignore
            }

            throw new Error(message);
        }


        const data =
            await response.json();


        displayHospitals(data);

    } catch (error) {

        console.error(error);

        result.innerHTML = `
            <div class="error-state">

                <h3>Unable to Load Hospitals</h3>

                <p>
                    ${escapeHtml(error.message)}
                </p>

            </div>
        `;
    }
}


/* =========================================================
   DISPLAY HOSPITALS
   ========================================================= */

function displayHospitals(data) {

    const result =
        document.getElementById("hospitalResult");


    /*
     * Depending on your backend pagination response,
     * the actual list may be inside:
     *
     * data.content
     * data.items
     * data.hospitals
     * or directly data
     */

    let hospitals = [];


    if (Array.isArray(data)) {

        hospitals = data;

    } else if (Array.isArray(data.content)) {

        hospitals = data.content;

    } else if (Array.isArray(data.items)) {

        hospitals = data.items;

    } else if (Array.isArray(data.hospitals)) {

        hospitals = data.hospitals;
    }


    if (hospitals.length === 0) {

        result.innerHTML = `
            <div class="empty-state">

                <h3>No Hospitals Found</h3>

                <p>
                    Try another city or disease ID.
                </p>

            </div>
        `;

        return;
    }


    result.innerHTML = `

        <div class="hospital-grid">

            ${hospitals
                .map(hospital => createHospitalCard(hospital))
                .join("")}

        </div>
    `;
}


/* =========================================================
   HOSPITAL CARD
   ========================================================= */

function createHospitalCard(hospital) {

    const name =
        hospital.name ??
        "Hospital";

    const city =
        hospital.city ??
        "";

    const address =
        hospital.address ??
        "Address not available";

    const phone =
        hospital.phone ??
        "Not available";

    const rating =
        hospital.rating ??
        "-";

    const emergency =
        hospital.emergencyAvailable ??
        false;


    return `

        <div class="hospital-card">

            <div class="hospital-icon">
                +
            </div>


            <h3>
                ${escapeHtml(name)}
            </h3>


            <p class="hospital-location">
                ${escapeHtml(city)}
            </p>


            <p>
                ${escapeHtml(address)}
            </p>


            <div class="hospital-info">

                <div>
                    <span>Rating</span>
                    <strong>
                        ${rating}
                    </strong>
                </div>

                <div>
                    <span>Emergency</span>
                    <strong>
                        ${emergency ? "Yes" : "No"}
                    </strong>
                </div>

            </div>


            <p class="hospital-phone">
                ${escapeHtml(phone)}
            </p>

        </div>
    `;
}


/* =========================================================
   UTILITY FUNCTIONS
   ========================================================= */

function formatNumber(value) {

    const number =
        Number(value);

    if (Number.isNaN(number)) {
        return value;
    }

    return number.toLocaleString("en-IN");
}


function escapeHtml(value) {

    if (value === null || value === undefined) {
        return "";
    }

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


/* =========================================================
   INITIAL MESSAGE
   ========================================================= */

console.log(
    "HealthWealth frontend loaded successfully."
);

console.log(
    "Backend API:",
    API_BASE_URL
);