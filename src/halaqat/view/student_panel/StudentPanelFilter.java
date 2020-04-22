package halaqat.view.student_panel;

import halaqat.AppConstants;
import halaqat.utils.Utils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StudentPanelFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        if (Utils.validateSession(AppConstants.STUDENT_USER, (HttpServletRequest) req))
            chain.doFilter(req, resp);
        else
            ((HttpServletResponse) resp).sendRedirect("/");
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
