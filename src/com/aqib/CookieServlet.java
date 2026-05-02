package com.aqib;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

/**
 * CookieServlet
 * Problem 37 - Display All Cookies
 * Concept Practiced: Cookie
 *
 * 1. Reads cookie Name and Value from the HTML form
 * 2. Creates a new Cookie with 1-day max age and adds it to the response
 * 3. Retrieves ALL cookies from the request using request.getCookies()
 * 4. Displays all cookies in an HTML table with Name and Value columns
 * 5. Shows a message if no cookies are present
 *
 * Author: Mohammed Aqib Martur
 * USN   : 2BL23CS190
 */
@WebServlet("/CookieServlet")
public class CookieServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------ GET
    // Redirect any direct GET request back to the form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("index.html");
    }

    // ----------------------------------------------------------------- POST
    // Called when the HTML form is submitted
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        /* ---- Step 1: Read form parameters ---- */
        String cookieName  = request.getParameter("cookieName");
        String cookieValue = request.getParameter("cookieValue");

        /* ---- Step 2: Input Validation ---- */
        if (cookieName == null || cookieName.trim().isEmpty()
                || cookieValue == null || cookieValue.trim().isEmpty()) {
            sendError(out, "Both Cookie Name and Cookie Value are required.");
            return;
        }

        cookieName  = cookieName.trim();
        cookieValue = cookieValue.trim();

        // Cookie names must not contain spaces or special characters
        if (cookieName.contains(" ") || cookieName.contains(";") || cookieName.contains("=")) {
            sendError(out, "Cookie name must not contain spaces or special characters (; = ).");
            return;
        }

        /* ---- Step 3: Create new Cookie and add to response ---- */
        Cookie newCookie = new Cookie(cookieName, cookieValue);
        newCookie.setMaxAge(24 * 60 * 60); // 1-day max age in seconds
        response.addCookie(newCookie);     // send cookie to browser

        /* ---- Step 4: Retrieve ALL cookies from the request ---- */
        // As per coding hint: Cookie[] all = request.getCookies();
        // Note: the newly added cookie won't appear in THIS request's getCookies()
        // because the browser sends cookies from the previous request.
        // It will appear from the next request onward — this is standard HTTP behaviour.
        Cookie[] all = request.getCookies();

        /* ---- Step 5: Build HTML response with cookies table ---- */
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>All Cookies</title>");
        out.println("<style>");
        out.println("*{box-sizing:border-box;margin:0;padding:0}");
        out.println("body{font-family:'Segoe UI',sans-serif;background:linear-gradient(135deg,#1a1a2e,#16213e,#0f3460);min-height:100vh;display:flex;align-items:center;justify-content:center;padding:20px}");
        out.println(".card{background:#fff;border-radius:16px;box-shadow:0 20px 60px rgba(0,0,0,.3);padding:40px;width:100%;max-width:540px}");
        out.println(".header{text-align:center;margin-bottom:24px}");
        out.println(".icon{font-size:42px;display:block;margin-bottom:10px}");
        out.println("h1{color:#1a1a2e;font-size:22px;font-weight:700}");
        out.println(".badge{background:#f0fff4;border:1px solid #28a745;border-radius:8px;padding:10px 16px;text-align:center;color:#1a7a35;font-weight:600;font-size:13px;margin-bottom:22px}");
        out.println(".saved-box{background:#f0f4ff;border-left:4px solid #0f3460;border-radius:0 8px 8px 0;padding:10px 14px;margin-bottom:22px;font-size:13px;color:#444}");
        out.println(".saved-box span{font-weight:700;color:#0f3460}");
        out.println("h2{color:#1a1a2e;font-size:16px;font-weight:700;margin-bottom:12px}");
        out.println("table{width:100%;border-collapse:collapse;font-size:14px;margin-bottom:22px}");
        out.println("th{background:#0f3460;color:#fff;padding:11px 16px;text-align:left;font-weight:600}");
        out.println("th:first-child{border-radius:6px 0 0 0}th:last-child{border-radius:0 6px 0 0}");
        out.println("td{padding:10px 16px;border-bottom:1px solid #eee;color:#222}");
        out.println("tr:nth-child(even) td{background:#f8f9ff}");
        out.println(".no-cookie{text-align:center;padding:24px;color:#999;font-size:14px;background:#f8f9ff;border-radius:8px;margin-bottom:22px}");
        out.println(".btn{display:block;text-align:center;padding:13px;background:linear-gradient(135deg,#0f3460,#533483);color:#fff;border-radius:8px;text-decoration:none;font-weight:600;font-size:15px}");
        out.println("</style></head><body>");
        out.println("<div class='card'>");
        out.println("  <div class='header'><span class='icon'>&#127850;</span><h1>Cookie Inspector</h1></div>");
        out.println("  <div class='badge'>&#10003; Cookie saved successfully!</div>");
        out.println("  <div class='saved-box'>New cookie saved &nbsp;&rarr;&nbsp; Name: <span>" + escapeHtml(cookieName) + "</span> &nbsp;|&nbsp; Value: <span>" + escapeHtml(cookieValue) + "</span> &nbsp;|&nbsp; Max Age: <span>1 day</span></div>");
        out.println("  <h2>&#128203; All Current Cookies</h2>");

        /* ---- Step 6: Display table or no-cookie message ---- */
        if (all == null || all.length == 0) {
            // Show message if no cookies are present
            out.println("  <div class='no-cookie'>&#9888; No cookies found in this request.<br><small>Your new cookie will appear on the next page load.</small></div>");
        } else {
            out.println("  <table>");
            out.println("    <tr><th>#</th><th>Cookie Name</th><th>Cookie Value</th></tr>");
            // Loop through all cookies as per coding hint
            int count = 1;
            for (Cookie c : all) {
                out.println("    <tr><td>" + count++ + "</td><td>" + escapeHtml(c.getName()) + "</td><td>" + escapeHtml(c.getValue()) + "</td></tr>");
            }
            out.println("  </table>");
        }

        out.println("  <a href='index.html' class='btn'>&#43; Add Another Cookie</a>");
        out.println("</div></body></html>");
    }

    /* ---- Utility: HTML escaping to prevent XSS ---- */
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    /* ---- Utility: styled error page ---- */
    private void sendError(PrintWriter out, String message) {
        out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Error</title>");
        out.println("<style>body{font-family:sans-serif;display:flex;align-items:center;justify-content:center;min-height:100vh;margin:0;background:#1a1a2e}");
        out.println(".box{background:#fff;border-radius:12px;padding:36px;max-width:420px;text-align:center}");
        out.println("h2{color:#c0392b;margin-bottom:14px}p{color:#555;margin-bottom:20px}");
        out.println("a{color:#fff;background:#0f3460;padding:10px 24px;border-radius:8px;text-decoration:none;font-weight:600}</style></head><body>");
        out.println("<div class='box'><h2>&#9888; Input Error</h2><p>" + message + "</p><a href='index.html'>Go Back</a></div></body></html>");
    }
}
