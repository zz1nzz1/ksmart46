package ksmart.mybatis.interceptor;

import java.util.Set;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CommonInterceptor implements HandlerInterceptor{
	
	private static final Logger log = LoggerFactory.getLogger(CommonInterceptor.class);

	/*
	 * preHandle: HandlerMapping이 핸들러 객체를 결정한 후 HandlerAdapter가 핸들러를 호출하기 전에 호출되는 메소드
	 * return이 true일 경우 핸들러 메서드 실행, false일 경우 핸들러 메서드 실행X 핸들러 진입X 
	 * */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//파라미터 값을 확인
		Set<String> paramKeySet = request.getParameterMap().keySet();
		
		StringJoiner param = new StringJoiner(",");
		
		for(String key : paramKeySet) {
			param.add(key + ":" + request.getParameter(key));
		}
		
		log.info("ACCESS INFO=================================================");
		log.info("PORT				::::::		{}", request.getLocalPort());
		log.info("ServerName				::::::		{}", request.getServerName());
		log.info("HTTPMethod				::::::		{}", request.getMethod());
		log.info("URI					::::::		{}", request.getRequestURI());
		log.info("CLIENT IP				::::::		{}", request.getRemoteAddr());
		log.info("Parameter				::::::		{}", param);
		log.info("ACCESS INFO=================================================");
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
		//return true; 의미와 동일함
	}
	
	/*
	 * postHandle: HandlerAdapter가 실제로 핸들러를 호출한 후 DispatcherServlet이 뷰가 전달되기 전에 호출되는 메소드  
	 * */
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	/*
	 * afterCompletion: HandlerAdapter가 실제로 핸들러를 호출한 후 DispatcherServlet이 뷰를 렌더링한 후에 호출되는 메소드
	 * */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}

}
