package lab4.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        String login = (session != null) ? (String) session.getAttribute("login") : null;
        String role  = (session != null) ? (String) session.getAttribute("role")  : null;

        String uri = req.getRequestURI();

        if (login == null) {

            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (uri.contains("/admin") && !"ADMIN".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}
