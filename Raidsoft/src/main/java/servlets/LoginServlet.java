package servlets;

import Conexion.ConexionBD;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import org.json.JSONObject;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private static final String SECRET_KEY = "6Lcu1t0rAAAAAGQz_HsAAKTcTNOvW4CmZkQKrI0p";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String clave = request.getParameter("clave");

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        //  Validar el reCAPTCHA
        boolean verificado = verifyRecaptcha(gRecaptchaResponse);

        if (!verificado) {
            request.setAttribute("mensaje", "Por favor verifica el reCAPTCHA");
            request.getRequestDispatcher("Login.jsp").forward(request, response);
            return; // 🔹 Importante: detener ejecución si no pasó el captcha
        }

        try (Connection con = ConexionBD.getConnection()) {
            String sql = "SELECT c.id_usuario, u.rol FROM cuentas c "
                       + "INNER JOIN usuarios u ON c.id_usuario = u.id_usuario "
                       + "WHERE c.usuario=? AND c.clave=MD5(?)";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, usuario);
            pst.setString(2, clave);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String rol = rs.getString("rol");
                HttpSession sesion = request.getSession();
                sesion.setAttribute("usuario", usuario);
                sesion.setAttribute("rol", rol);

                if ("admin".equals(rol)) {
                    response.sendRedirect("Admin.jsp");
                } else {
                    response.sendRedirect("Vendedor.jsp");
                }
            } else {
                request.setAttribute("mensaje", "Usuario o contraseña incorrectos");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensaje", "Error en el servidor");
            request.getRequestDispatcher("Login.jsp").forward(request, response);
        }
    }

    private boolean verifyRecaptcha(String gRecaptchaResponse) {
        try {
            String url = "https://www.google.com/recaptcha/api/siteverify";
            String params = "secret=" + SECRET_KEY + "&response=" + gRecaptchaResponse;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
                out.writeBytes(params);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            return json.getBoolean("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
