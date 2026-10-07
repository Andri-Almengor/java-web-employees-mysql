<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <title>Empleados</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 40px;
        }

        .container {
            max-width: 800px;
            margin: auto;
            background-color: white;
            padding: 30px;
            border-radius: 10px;
        }

        h1 {
            text-align: center;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th,
        td {
            border: 1px solid #dddddd;
            padding: 12px;
            text-align: left;
        }

        th {
            background-color: #333333;
            color: white;
        }

        tr:nth-child(even) {
            background-color: #f2f2f2;
        }

        .error {
            color: red;
            text-align: center;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>Lista de empleados</h1>

    <%
        String error = (String) request.getAttribute("error");

        if (error != null) {
    %>

        <p class="error">
            <%= error %>
        </p>

    <%
        }

        List<String[]> empleados =
            (List<String[]>) request.getAttribute("empleados");

        if (empleados != null && !empleados.isEmpty()) {
    %>

    <table>

        <thead>

            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Departamento</th>
            </tr>

        </thead>

        <tbody>

        <%
            for (String[] empleado : empleados) {
        %>

            <tr>
                <td><%= empleado[0] %></td>
                <td><%= empleado[1] %></td>
                <td><%= empleado[2] %></td>
            </tr>

        <%
            }
        %>

        </tbody>

    </table>

    <%
        } else if (error == null) {
    %>

        <p>No existen empleados registrados.</p>

    <%
        }
    %>

</div>

</body>
</html>