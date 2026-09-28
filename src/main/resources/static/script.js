// Transit Fleet Management — dashboard frontend
// Talks to the Spring Boot REST API served from the same origin (no base URL needed
// because this file is served from src/main/resources/static by the same app).

const api = {
    async get(path) {
        const res = await fetch(path);
        if (!res.ok) throw await toError(res);
        return res.json();
    },
    async post(path, body) {
        const res = await fetch(path, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body),
        });
        if (!res.ok) throw await toError(res);
        return res.status === 204 ? null : res.json();
    },
};

async function toError(res) {
    try {
        const data = await res.json();
        return new Error(data.message || `Request failed (${res.status})`);
    } catch {
        return new Error(`Request failed (${res.status})`);
    }
}

// ---------- Tab navigation ----------
document.querySelectorAll(".nav-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
        document.querySelectorAll(".nav-btn").forEach((b) => b.classList.remove("active"));
        document.querySelectorAll(".tab").forEach((t) => t.classList.remove("active"));
        btn.classList.add("active");
        document.getElementById("tab-" + btn.dataset.tab).classList.add("active");
        onTabShown(btn.dataset.tab);
    });
});

function onTabShown(tab) {
    if (tab === "dashboard") loadDashboard();
    if (tab === "buses") loadBuses();
    if (tab === "routes") loadRoutes();
    if (tab === "contacts") loadContacts();
    if (tab === "tickets") loadTicketingTab();
    if (tab === "purchasing") loadPurchasingTab();
    if (tab === "reports") loadReportsTab();
}

// ---------- Dashboard ----------
async function loadDashboard() {
    try {
        const [buses, routes, tickets, bills] = await Promise.all([
            api.get("/api/buses"),
            api.get("/api/routes"),
            api.get("/api/tickets"),
            api.get("/api/vendor-bills"),
        ]);
        document.getElementById("stat-buses").textContent = buses.length;
        document.getElementById("stat-routes").textContent = routes.length;
        document.getElementById("stat-tickets").textContent = tickets.length;
        document.getElementById("stat-bills").textContent = bills.filter((b) => b.status === "UNPAID").length;
    } catch (e) {
        console.error(e);
    }
}

// ---------- Buses ----------
async function loadBuses() {
    const buses = await api.get("/api/buses");
    const tbody = document.querySelector("#table-buses tbody");
    tbody.innerHTML = buses
        .map(
            (b) => `<tr>
        <td>${b.id}</td><td>${b.registrationNumber}</td><td>${b.model || ""}</td>
        <td>${b.capacity}</td><td>${b.status}</td>
      </tr>`
        )
        .join("");
}

document.getElementById("form-bus").addEventListener("submit", async (e) => {
    e.preventDefault();
    const form = e.target;
    const payload = {
        registrationNumber: form.registrationNumber.value,
        model: form.model.value,
        capacity: Number(form.capacity.value),
        status: form.status.value,
    };
    await api.post("/api/buses", payload);
    form.reset();
    loadBuses();
});

// ---------- Routes ----------
async function loadRoutes() {
    const routes = await api.get("/api/routes");
    const tbody = document.querySelector("#table-routes tbody");
    tbody.innerHTML = routes
        .map(
            (r) => `<tr>
        <td>${r.id}</td><td>${r.routeCode}</td><td>${r.routeName}</td>
        <td>${r.startPoint || ""}</td><td>${r.endPoint || ""}</td>
        <td>${r.distanceKm ?? ""}</td><td>${r.baseFare ?? ""}</td>
      </tr>`
        )
        .join("");
}

document.getElementById("form-route").addEventListener("submit", async (e) => {
    e.preventDefault();
    const form = e.target;
    const payload = {
        routeCode: form.routeCode.value,
        routeName: form.routeName.value,
        startPoint: form.startPoint.value,
        endPoint: form.endPoint.value,
        distanceKm: form.distanceKm.value ? Number(form.distanceKm.value) : null,
        baseFare: Number(form.baseFare.value),
    };
    await api.post("/api/routes", payload);
    form.reset();
    loadRoutes();
});

// ---------- Contacts ----------
async function loadContacts() {
    const contacts = await api.get("/api/contacts");
    const tbody = document.querySelector("#table-contacts tbody");
    tbody.innerHTML = contacts
        .map(
            (c) => `<tr>
        <td>${c.id}</td><td>${c.name}</td><td>${c.type}</td>
        <td>${c.phone || ""}</td><td>${c.email || ""}</td>
      </tr>`
        )
        .join("");
}

document.getElementById("form-contact").addEventListener("submit", async (e) => {
    e.preventDefault();
    const form = e.target;
    const payload = {
        name: form.name.value,
        type: form.type.value,
        phone: form.phone.value,
        email: form.email.value,
    };
    await api.post("/api/contacts", payload);
    form.reset();
    loadContacts();
});

// ---------- Ticketing ----------
async function loadTicketingTab() {
    const [contacts, routes, buses, tickets] = await Promise.all([
        api.get("/api/contacts"),
        api.get("/api/routes"),
        api.get("/api/buses"),
        api.get("/api/tickets"),
    ]);

    fillSelect(
        "select-passenger",
        contacts.filter((c) => c.type === "PASSENGER"),
        (c) => c.id,
        (c) => c.name
    );
    fillSelect("select-route", routes, (r) => r.id, (r) => `${r.routeCode} - ${r.routeName}`);
    fillSelect("select-bus", buses, (b) => b.id, (b) => b.registrationNumber);

    renderTickets(tickets);
}

function fillSelect(id, items, valueFn, labelFn) {
    const select = document.getElementById(id);
    const placeholder = select.querySelector("option[value='']");
    select.innerHTML = "";
    if (placeholder) select.appendChild(placeholder);
    items.forEach((item) => {
        const opt = document.createElement("option");
        opt.value = valueFn(item);
        opt.textContent = labelFn(item);
        select.appendChild(opt);
    });
}

function renderTickets(tickets) {
    const tbody = document.querySelector("#table-tickets tbody");
    tbody.innerHTML = tickets
        .map((t) => {
            const actions =
                t.status === "CONFIRMED"
                    ? `<button class="success" onclick="collectFare(${t.id})">Invoice &amp; Collect Payment</button>
             <button class="danger" onclick="cancelTicket(${t.id})">Cancel</button>`
                    : "";
            return `<tr>
        <td>${t.bookingRef}</td><td>${t.passenger?.name ?? ""}</td>
        <td>${t.route?.routeCode ?? ""}</td><td>${t.bus?.registrationNumber ?? ""}</td>
        <td>${t.seatNumber}</td><td>${t.travelDate}</td><td>${t.fare}</td>
        <td>${t.status}</td><td>${actions}</td>
      </tr>`;
        })
        .join("");
}

document.getElementById("form-ticket").addEventListener("submit", async (e) => {
    e.preventDefault();
    const form = e.target;
    const feedback = document.getElementById("ticket-feedback");
    feedback.textContent = "";
    feedback.className = "feedback";

    const payload = {
        passengerId: Number(form.passengerId.value),
        routeId: Number(form.routeId.value),
        busId: Number(form.busId.value),
        seatNumber: form.seatNumber.value,
        travelDate: form.travelDate.value,
    };

    try {
        await api.post("/api/tickets", payload);
        feedback.textContent = "Ticket booked successfully.";
        feedback.className = "feedback success";
        form.reset();
        loadTicketingTab();
    } catch (err) {
        feedback.textContent = err.message;
        feedback.className = "feedback error";
    }
});

async function cancelTicket(id) {
    await api.post(`/api/tickets/${id}/cancel`, {});
    loadTicketingTab();
}

// Generates the passenger's invoice for a booking, then immediately collects
// the fare by cash. Adjust the payment mode here if you want a bank/digital flow instead.
async function collectFare(salesOrderId) {
    try {
        const invoice = await api.post(`/api/invoices/from-ticket/${salesOrderId}`, {});
        await api.post(`/api/payments/receive/${invoice.id}`, { mode: "CASH" });
        alert(`Invoice ${invoice.invoiceNumber} generated and fare collected (cash).`);
        loadTicketingTab();
    } catch (err) {
        alert("Error: " + err.message);
    }
}

// ---------- Purchasing (fuel & maintenance) ----------
async function loadPurchasingTab() {
    const [contacts, products, purchaseOrders, bills] = await Promise.all([
        api.get("/api/contacts"),
        api.get("/api/products"),
        api.get("/api/purchase-orders"),
        api.get("/api/vendor-bills"),
    ]);

    fillSelect(
        "select-vendor",
        contacts.filter((c) => c.type === "MAINTENANCE_CONTRACTOR" || c.type === "FUEL_VENDOR"),
        (c) => c.id,
        (c) => `${c.name} (${c.type})`
    );
    fillSelect(
        "select-product",
        products,
        (p) => p.id,
        (p) => `${p.name} — ${p.unitPrice}/unit`
    );

    renderPurchaseOrders(purchaseOrders);
    renderBills(bills);
}

function renderPurchaseOrders(pos) {
    const tbody = document.querySelector("#table-po tbody");
    tbody.innerHTML = pos
        .map(
            (po) => `<tr>
        <td>${po.poNumber}</td><td>${po.vendor?.name ?? ""}</td><td>${po.product?.name ?? ""}</td>
        <td>${po.quantity}</td><td>${po.totalAmount}</td><td>${po.status}</td>
        <td><button onclick="convertToBill(${po.id})">Convert to Bill</button></td>
      </tr>`
        )
        .join("");
}

function renderBills(bills) {
    const tbody = document.querySelector("#table-bills tbody");
    tbody.innerHTML = bills
        .map((bill) => {
            const action =
                bill.status === "UNPAID"
                    ? `<button class="success" onclick="payBill(${bill.id})">Pay (Bank)</button>`
                    : "";
            return `<tr>
        <td>${bill.billNumber}</td><td>${bill.vendor?.name ?? ""}</td>
        <td>${bill.amount}</td><td>${bill.status}</td><td>${action}</td>
      </tr>`;
        })
        .join("");
}

document.getElementById("form-po").addEventListener("submit", async (e) => {
    e.preventDefault();
    const form = e.target;
    const payload = {
        vendorId: Number(form.vendorId.value),
        productId: Number(form.productId.value),
        quantity: Number(form.quantity.value),
    };
    await api.post("/api/purchase-orders", payload);
    form.reset();
    loadPurchasingTab();
});

async function convertToBill(purchaseOrderId) {
    try {
        await api.post(`/api/vendor-bills/from-po/${purchaseOrderId}`, {});
        loadPurchasingTab();
    } catch (err) {
        alert("Error: " + err.message);
    }
}

async function payBill(billId) {
    try {
        await api.post(`/api/payments/pay/${billId}`, { mode: "BANK" });
        loadPurchasingTab();
    } catch (err) {
        alert("Error: " + err.message);
    }
}

// ---------- Reports ----------
async function loadReportsTab() {
    const routes = await api.get("/api/routes");
    fillSelect("util-route", routes, (r) => r.id, (r) => `${r.routeCode} - ${r.routeName}`);
}

document.getElementById("btn-pnl").addEventListener("click", async () => {
    const from = document.getElementById("pnl-from").value;
    const to = document.getElementById("pnl-to").value;
    const params = new URLSearchParams();
    if (from) params.set("from", from);
    if (to) params.set("to", to);
    const data = await api.get(`/api/reports/profit-and-loss?${params}`);
    document.getElementById("report-pnl").textContent = JSON.stringify(data, null, 2);
});

document.getElementById("btn-bs").addEventListener("click", async () => {
    const asOfDate = document.getElementById("bs-date").value;
    const params = new URLSearchParams();
    if (asOfDate) params.set("asOfDate", asOfDate);
    const data = await api.get(`/api/reports/balance-sheet?${params}`);
    document.getElementById("report-bs").textContent = JSON.stringify(data, null, 2);
});

document.getElementById("btn-budget").addEventListener("click", async () => {
    const data = await api.get("/api/reports/budget-variance");
    document.getElementById("report-budget").textContent = JSON.stringify(data, null, 2);
});

document.getElementById("btn-util").addEventListener("click", async () => {
    const routeId = document.getElementById("util-route").value;
    if (!routeId) {
        alert("Choose a route first.");
        return;
    }
    const from = document.getElementById("util-from").value;
    const to = document.getElementById("util-to").value;
    const params = new URLSearchParams();
    if (from) params.set("from", from);
    if (to) params.set("to", to);
    const data = await api.get(`/api/reports/route-utilization/${routeId}?${params}`);
    document.getElementById("report-util").textContent = JSON.stringify(data, null, 2);
});

// ---------- Initial load ----------
loadDashboard();