package com.empresa;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/employees")
public class EmployeeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        List<String[]> empleados = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            Statement statement = connection.createStatement()
        ) {

            /*
             * Revisamos si existen empleados.
             */
            ResultSet countResult =
                    statement.executeQuery("SELECT COUNT(*) FROM empleados");

            countResult.next();

            int cantidad = countResult.getInt(1);

            /*
             * Si la tabla está vacía,
             * agregamos algunos empleados utilizando Statement.
             */
            if (cantidad == 0) {

                statement.executeUpdate(
                    "INSERT INTO empleados (nombre, departamento) " +
                    "VALUES ('Carlos Rodríguez', 'Ventas')"
                );

                statement.executeUpdate(
                    "INSERT INTO empleados (nombre, departamento) " +
                    "VALUES ('María González', 'Recursos Humanos')"
                );

                statement.executeUpdate(
                    "INSERT INTO empleados (nombre, departamento) " +
                    "VALUES ('Andrés López', 'Tecnología')"
                );
            }

            /*
             * Consulta de empleados.
             */
            ResultSet result =
                    statement.executeQuery(
                        "SELECT id, nombre, departamento FROM empleados"
                    );

            while (result.next()) {

                String id =
                        String.valueOf(result.getInt("id"));

                String nombre =
                        result.getString("nombre");

                String departamento =
                        result.getString("departamento");

                empleados.add(
                    new String[]{id, nombre, departamento}
                );
            }

            request.setAttribute("empleados", empleados);

            request.getRequestDispatcher("/employee.jsp")
                   .forward(request, response);

        } catch (SQLException e) {

            e.printStackTrace();

            request.setAttribute(
                "error",
                "Error al conectar o consultar la base de datos: "
                + e.getMessage()
            );

            request.getRequestDispatcher("/employee.jsp")
                   .forward(request, response);
        }
    }
}