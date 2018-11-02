package tarefa6;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.esfinge.aom.api.model.IEntity;
import org.esfinge.aom.model.dynamic.factory.AdapterFactory;

public class ValidadorProduto {

	public boolean validarEntidadeCobranca(IEntity produto) throws Exception {
		
		// Transformando para Java Bean
		AdapterFactory af = AdapterFactory.getInstance("AnnotationMapping.json");
		Object exemploJavaBean = af.generate(produto);

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Object>> violations = validator.validate(exemploJavaBean);
		for (ConstraintViolation<Object> violation : violations) {
			System.out.println(violation.getMessage());
			return false;
		}
		return true;
	}

}
