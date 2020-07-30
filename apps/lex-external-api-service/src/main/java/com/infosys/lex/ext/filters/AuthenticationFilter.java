package com.infosys.lex.ext.filters;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.google.common.base.Optional;
import com.infosys.lex.ext.service.UserDataUtil;

import org.sunbird.common.responsecode.ResponseCode;

@Component
public class AuthenticationFilter extends GenericFilterBean {

	private final String clientHeader="Client_Id";
	private final String apiHeader="Api_Key";
	
	@Autowired
	UserDataUtil dataUtil;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) (request);
		HttpServletResponse httpResponse = (HttpServletResponse) (response);
		try {
			Optional<String> clientId = Optional.fromNullable(httpRequest.getHeader(clientHeader));
			Optional<String> apiKey = Optional.fromNullable(httpRequest.getHeader(apiHeader));
			if (!clientId.isPresent()) {
				httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
						MessageFormat.format(ResponseCode.mandatoryHeadersMissing.getErrorMessage(), clientHeader));
				return;
			}
			if (!apiKey.isPresent()) {
				httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
						MessageFormat.format(ResponseCode.mandatoryHeadersMissing.getErrorMessage(), apiHeader));
				return;
			}

			String authorization = dataUtil.getAuthorization(clientId.get(), apiKey.get(),
					httpRequest.getServletPath());

			if (authorization.equals("unauthorized")) {
				httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
						ResponseCode.UNAUTHORIZED.getErrorMessage());
				return;
			}
			if (authorization.equals("invalidUrl")) {
				httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND,
						ResponseCode.resourceNotFound.getErrorMessage());
				return;
			}
			chain.doFilter(request, response);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, ResponseCode.UNAUTHORIZED.getErrorMessage());
		}
	}
}
