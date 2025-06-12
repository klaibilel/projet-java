<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en"/>
<fmt:setBundle basename="messages"/>

<!DOCTYPE html>
<html lang="${pageContext.request.locale.language}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="app.title"/></title>
    
    <!-- CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
    
    <!-- Favicon -->
    <link rel="icon" type="image/x-icon" href="images/favicon.ico">
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="index.jsp">
                <i class="fas fa-hive me-2"></i>
                <fmt:message key="app.title"/>
            </a>
            
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link active" href="index.jsp">
                            <i class="fas fa-tachometer-alt me-1"></i>
                            <fmt:message key="nav.dashboard"/>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="beehives.jsp">
                            <i class="fas fa-hive me-1"></i>
                            <fmt:message key="nav.beehives"/>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="sites.jsp">
                            <i class="fas fa-map-marker-alt me-1"></i>
                            <fmt:message key="nav.sites"/>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="farms.jsp">
                            <i class="fas fa-seedling me-1"></i>
                            <fmt:message key="nav.farms"/>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="agents.jsp">
                            <i class="fas fa-users me-1"></i>
                            <fmt:message key="nav.agents"/>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="visits.jsp">
                            <i class="fas fa-calendar-check me-1"></i>
                            <fmt:message key="nav.visits"/>
                        </a>
                    </li>
                </ul>
                
                <!-- Language Selector -->
                <div class="dropdown me-3">
                    <button class="btn btn-outline-light dropdown-toggle" type="button" data-bs-toggle="dropdown">
                        <i class="fas fa-globe me-1"></i>
                        ${pageContext.request.locale.language.toUpperCase()}
                    </button>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" href="?lang=fr">Français</a></li>
                        <li><a class="dropdown-item" href="?lang=en">English</a></li>
                        <li><a class="dropdown-item" href="?lang=ar">العربية</a></li>
                    </ul>
                </div>
                
                <!-- User Menu -->
                <div class="dropdown">
                    <button class="btn btn-outline-light dropdown-toggle" type="button" data-bs-toggle="dropdown">
                        <i class="fas fa-user me-1"></i>
                        ${sessionScope.user != null ? sessionScope.user.fullName : 'Guest'}
                    </button>
                    <ul class="dropdown-menu">
                        <c:if test="${sessionScope.user == null}">
                            <li><a class="dropdown-item" href="auth/login">
                                <i class="fas fa-sign-in-alt me-1"></i>
                                <fmt:message key="auth.login"/>
                            </a></li>
                        </c:if>
                        <c:if test="${sessionScope.user != null}">
                            <li><a class="dropdown-item" href="settings.jsp">
                                <i class="fas fa-cog me-1"></i>
                                <fmt:message key="nav.settings"/>
                            </a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="auth/logout">
                                <i class="fas fa-sign-out-alt me-1"></i>
                                <fmt:message key="nav.logout"/>
                            </a></li>
                        </c:if>
                    </ul>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container-fluid mt-4">
        <!-- Welcome Section -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card bg-gradient-primary text-white">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <h1 class="card-title mb-2">
                                    <i class="fas fa-hive me-2"></i>
                                    <fmt:message key="app.welcome"/>
                                </h1>
                                <p class="card-text lead mb-0">
                                    <fmt:message key="app.description"/>
                                </p>
                            </div>
                            <div class="col-md-4 text-end">
                                <i class="fas fa-bee display-1 opacity-25"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Statistics Cards -->
        <div class="row mb-4">
            <div class="col-xl-3 col-md-6 mb-4">
                <div class="card border-left-primary shadow h-100 py-2">
                    <div class="card-body">
                        <div class="row no-gutters align-items-center">
                            <div class="col mr-2">
                                <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                    <fmt:message key="nav.beehives"/>
                                </div>
                                <div class="h5 mb-0 font-weight-bold text-gray-800" id="totalBeehives">
                                    <i class="fas fa-spinner fa-spin"></i>
                                </div>
                            </div>
                            <div class="col-auto">
                                <i class="fas fa-hive fa-2x text-gray-300"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-xl-3 col-md-6 mb-4">
                <div class="card border-left-success shadow h-100 py-2">
                    <div class="card-body">
                        <div class="row no-gutters align-items-center">
                            <div class="col mr-2">
                                <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                    <fmt:message key="dashboard.high.production"/>
                                </div>
                                <div class="h5 mb-0 font-weight-bold text-gray-800" id="highProductionBeehives">
                                    <i class="fas fa-spinner fa-spin"></i>
                                </div>
                            </div>
                            <div class="col-auto">
                                <i class="fas fa-arrow-up fa-2x text-gray-300"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-xl-3 col-md-6 mb-4">
                <div class="card border-left-warning shadow h-100 py-2">
                    <div class="card-body">
                        <div class="row no-gutters align-items-center">
                            <div class="col mr-2">
                                <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                                    <fmt:message key="dashboard.scheduled.visits"/>
                                </div>
                                <div class="h5 mb-0 font-weight-bold text-gray-800" id="scheduledVisits">
                                    <i class="fas fa-spinner fa-spin"></i>
                                </div>
                            </div>
                            <div class="col-auto">
                                <i class="fas fa-calendar-check fa-2x text-gray-300"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-xl-3 col-md-6 mb-4">
                <div class="card border-left-danger shadow h-100 py-2">
                    <div class="card-body">
                        <div class="row no-gutters align-items-center">
                            <div class="col mr-2">
                                <div class="text-xs font-weight-bold text-danger text-uppercase mb-1">
                                    <fmt:message key="dashboard.alerts"/>
                                </div>
                                <div class="h5 mb-0 font-weight-bold text-gray-800" id="alerts">
                                    <i class="fas fa-spinner fa-spin"></i>
                                </div>
                            </div>
                            <div class="col-auto">
                                <i class="fas fa-exclamation-triangle fa-2x text-gray-300"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Charts and Tables Row -->
        <div class="row">
            <!-- Production Chart -->
            <div class="col-xl-8 col-lg-7">
                <div class="card shadow mb-4">
                    <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <fmt:message key="dashboard.overview"/>
                        </h6>
                        <div class="dropdown no-arrow">
                            <a class="dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                                <i class="fas fa-ellipsis-v fa-sm fa-fw text-gray-400"></i>
                            </a>
                            <div class="dropdown-menu dropdown-menu-right shadow">
                                <a class="dropdown-item" href="#">
                                    <fmt:message key="form.export"/>
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="chart-area">
                            <canvas id="productionChart"></canvas>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Recent Visits -->
            <div class="col-xl-4 col-lg-5">
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <fmt:message key="dashboard.scheduled.visits"/>
                        </h6>
                    </div>
                    <div class="card-body">
                        <div id="recentVisits">
                            <div class="text-center">
                                <i class="fas fa-spinner fa-spin fa-2x text-gray-300"></i>
                                <p class="text-gray-500 mt-2">Chargement...</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- High/Low Production Tables -->
        <div class="row">
            <div class="col-lg-6 mb-4">
                <div class="card shadow">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-success">
                            <i class="fas fa-arrow-up me-1"></i>
                            <fmt:message key="dashboard.high.production"/>
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered" id="highProductionTable">
                                <thead>
                                    <tr>
                                        <th><fmt:message key="beehive.name"/></th>
                                        <th><fmt:message key="beehive.site"/></th>
                                        <th><fmt:message key="beehive.honey.quantity"/></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="3" class="text-center">
                                            <i class="fas fa-spinner fa-spin"></i> Chargement...
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-lg-6 mb-4">
                <div class="card shadow">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-warning">
                            <i class="fas fa-arrow-down me-1"></i>
                            <fmt:message key="dashboard.low.production"/>
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered" id="lowProductionTable">
                                <thead>
                                    <tr>
                                        <th><fmt:message key="beehive.name"/></th>
                                        <th><fmt:message key="beehive.site"/></th>
                                        <th><fmt:message key="beehive.honey.quantity"/></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="3" class="text-center">
                                            <i class="fas fa-spinner fa-spin"></i> Chargement...
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-light text-center text-lg-start mt-5">
        <div class="container p-4">
            <div class="row">
                <div class="col-lg-6 col-md-12 mb-4 mb-md-0">
                    <h5 class="text-uppercase">
                        <i class="fas fa-hive me-2"></i>
                        <fmt:message key="app.title"/>
                    </h5>
                    <p>
                        <fmt:message key="app.description"/>
                    </p>
                </div>
                <div class="col-lg-3 col-md-6 mb-4 mb-md-0">
                    <h5 class="text-uppercase">Liens</h5>
                    <ul class="list-unstyled mb-0">
                        <li><a href="#!" class="text-dark">Documentation</a></li>
                        <li><a href="#!" class="text-dark">Support</a></li>
                        <li><a href="#!" class="text-dark">API</a></li>
                    </ul>
                </div>
                <div class="col-lg-3 col-md-6 mb-4 mb-md-0">
                    <h5 class="text-uppercase">Contact</h5>
                    <ul class="list-unstyled mb-0">
                        <li><a href="mailto:support@smartbeehouse.com" class="text-dark">
                            <i class="fas fa-envelope me-1"></i> support@smartbeehouse.com
                        </a></li>
                        <li><a href="tel:+1234567890" class="text-dark">
                            <i class="fas fa-phone me-1"></i> +123 456 7890
                        </a></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="text-center p-3 bg-dark text-white">
            © 2024 Smart Bee House Management System. Tous droits réservés.
        </div>
    </footer>

    <!-- JavaScript -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="js/dashboard.js"></script>
</body>
</html>