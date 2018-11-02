package hello;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@ConfigurationProperties
@Component
@RefreshScope
public class PropertyConfiguration {

	private String [] exercicioparam;

	public String [] getExercicioparam() {
		return exercicioparam;
	}

	public void setExercicioparam(String [] exercicioparam) {
		this.exercicioparam = exercicioparam;
	}

}
