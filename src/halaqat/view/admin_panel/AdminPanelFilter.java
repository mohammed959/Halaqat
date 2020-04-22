package halaqat.view.admin_panel;

import halaqat.AppConstants;
import halaqat.utils.Utils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminPanelFilter implements Filter {


    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        if (Utils.validateSession(AppConstants.ADMIN_USER, (HttpServletRequest) req))
            chain.doFilter(req, resp);
        else
            ((HttpServletResponse) resp).sendRedirect("/");
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }


    @Override
    public void destroy() {

    }


}
