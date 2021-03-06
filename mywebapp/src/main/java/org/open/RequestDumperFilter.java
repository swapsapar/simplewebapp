package org.open;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Locale;


/**
 * Example filter that dumps interesting state information about a request
 * to the associated servlet context log file, before allowing the servlet
 * to process the request in the usual way.  This can be installed as needed
 * to assist in debugging problems.
 */

public final class RequestDumperFilter implements Filter {


    // ----------------------------------------------------- Instance Variables


    /**
     * The filter configuration object we are associated with.  If this value
     * is null, this filter instance is not currently configured.
     */
    private FilterConfig filterConfig = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Take this filter out of service.
     */
    public void destroy() {

        this.filterConfig = null;

    }


    /**
     * Time the processing that is performed by all subsequent filters in the
     * current filter stack, including the ultimately invoked servlet.
     *
     * @param request  The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain    The filter chain we are processing
     * @throws IOException      if an input/output error occurs
     * @throws ServletException if a servlet error occurs
     */
    public void doFilter(final ServletRequest request, final ServletResponse response,
                         final FilterChain chain)
            throws IOException, ServletException {

        if (filterConfig == null) {
            return;
        }

        // Render the generic servlet request properties
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        writer.println("Request Received at " +
                (new Timestamp(System.currentTimeMillis())));
        writer.println(" characterEncoding=" + request.getCharacterEncoding());
        writer.println("     contentLength=" + request.getContentLength());
        writer.println("       contentType=" + request.getContentType());
        writer.println("            locale=" + request.getLocale());
        writer.print("           locales=");
        final Enumeration locales = request.getLocales();
        boolean first = true;
        while (locales.hasMoreElements()) {
            final Locale locale = (Locale) locales.nextElement();
            if (first) {
                first = false;
            } else {
                writer.print(", ");
            }
            writer.print(locale.toString());
        }
        writer.println();
        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            final String name = (String) names.nextElement();
            writer.print("         parameter=" + name + "=");
            final String[] values = request.getParameterValues(name);
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    writer.print(", ");
                }
                writer.print(values[i]);
            }
            writer.println();
        }
        writer.println("          protocol=" + request.getProtocol());
        writer.println("        remoteAddr=" + request.getRemoteAddr());
        writer.println("        remoteHost=" + request.getRemoteHost());
        writer.println("            scheme=" + request.getScheme());
        writer.println("        serverName=" + request.getServerName());
        writer.println("        serverPort=" + request.getServerPort());
        writer.println("          isSecure=" + request.isSecure());

        // Render the HTTP servlet request properties
        if (request instanceof HttpServletRequest) {
            writer.println("---------------------------------------------");
            final HttpServletRequest hrequest = (HttpServletRequest) request;
            writer.println("       contextPath=" + hrequest.getContextPath());
            Cookie cookies[] = hrequest.getCookies();
            if (cookies == null) {
                cookies = new Cookie[0];
            }
            for (final Cookie cooky : cookies) {
                writer.println("            cookie=" + cooky.getName() +
                        "=" + cooky.getValue());
            }
            names = hrequest.getHeaderNames();
            while (names.hasMoreElements()) {
                final String name = (String) names.nextElement();
                final String value = hrequest.getHeader(name);
                writer.println("            header=" + name + "=" + value);
            }
            writer.println("            method=" + hrequest.getMethod());
            writer.println("          pathInfo=" + hrequest.getPathInfo());
            writer.println("       queryString=" + hrequest.getQueryString());
            writer.println("        remoteUser=" + hrequest.getRemoteUser());
            writer.println("requestedSessionId=" +
                    hrequest.getRequestedSessionId());
            writer.println("        requestURI=" + hrequest.getRequestURI());
            writer.println("       servletPath=" + hrequest.getServletPath());
        }
        writer.println("=============================================");

        // Log the resulting string
        writer.flush();
        filterConfig.getServletContext().log(sw.getBuffer().toString());

        // Pass control on to the next filter
        chain.doFilter(request, response);

    }


    /**
     * Place this filter into service.
     *
     * @param filterConfig The filter configuration object
     */
    public void init(final FilterConfig filterConfig) throws ServletException {

        this.filterConfig = filterConfig;

    }


    /**
     * Return a String representation of this object.
     */
    public String toString() {

        if (filterConfig == null) {
            return ("RequestDumperFilter()");
        }
        final StringBuilder sb;
        sb = new StringBuilder("RequestDumperFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());

    }


}
