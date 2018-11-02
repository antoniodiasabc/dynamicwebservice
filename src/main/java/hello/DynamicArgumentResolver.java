package hello;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class DynamicArgumentResolver implements HandlerMethodArgumentResolver {

	PropertyConfiguration propertyConfiguration;

	public DynamicArgumentResolver() {
		super();
	}

	public DynamicArgumentResolver(PropertyConfiguration pConfiguration) {
		super();
		propertyConfiguration = pConfiguration;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		if (parameter.getParameterIndex() == 0) {
			return parameter.getParameterType().equals(String.class);
		} else {
			return true;
		}
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		if (parameter.getParameterIndex() == 0) {
			return new String("ruleName");
		} else {
			String description = webRequest.getDescription(false);
			System.out.println(description);
			String[] exercicioparam = propertyConfiguration.getExercicioparam();
			Object[] objects = new Object[1];
			if (exercicioparam.length == 1) {
				objects[0] = exercicioparam[0];
			} else {
				objects[0] = exercicioparam[0];
				for (int i = 0; i < exercicioparam.length; i++) {
					if (exercicioparam[i] != null) {
						String valor = exercicioparam[i];
						String[] split = valor.split("_");
						if (split.length == 2) {
							if (description.contains(split[1])) {
								objects[0] = split[0];
								break;
							}
						}
					}
				}
			}
			return objects;
		}
	}
}