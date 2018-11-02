package hello;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.esfinge.aom.api.model.IEntity;
import org.esfinge.aom.api.model.IEntityType;
import org.esfinge.aom.api.model.IPropertyType;
import org.esfinge.aom.api.model.RuleObject;
import org.esfinge.aom.exceptions.EsfingeAOMException;
import org.esfinge.aom.model.dynamic.factory.AdapterFactory;
import org.esfinge.aom.model.impl.GenericPropertyType;
import org.esfinge.aom.model.rolemapper.core.AdapterEntityType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import entity.MetadatasAccountType;
import entity.SensorType;

@Component(value = "testeW3")
public class MeuBeanRegistration3 implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

	private static final String NOMEENDPOINT = "nomeendpoint3";
	BeanDefinitionRegistry registry;

	@Autowired
	Environment env;

	Map<String, Object> mapObjects = new HashMap<>();

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory arg0) throws BeansException {

	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		String command = "";
		String nomeEP = "";

		System.out.println("passou no postProcessBeanDefinitionRegistry do MeuBeanRegistration3 ");

		List<String> endPointNames = getEndPointName(NOMEENDPOINT);
		for (String nomeEndPoint : endPointNames) {
			if (nomeEndPoint != null) {
				String[] split = nomeEndPoint.split("/");
				if (split.length == 2) {
					command = split[0];
					nomeEP = "/" + split[1];
				}
			}

			Object exercicio1 = createWebServiceDynamicAdapt(command, nomeEP); 
			System.out.println(" comando " + command + "  classe [" + exercicio1.getClass() + "]");
			mapObjects.put(command, exercicio1);

			try {
				RootBeanDefinition beanDefinition = new RootBeanDefinition(exercicio1.getClass());
				beanDefinition.setTargetType(exercicio1.getClass());
				beanDefinition.setRole(BeanDefinition.ROLE_APPLICATION);
				beanDefinition.setBeanClass(exercicio1.getClass());
				beanDefinition.setFactoryMethodName("handleResults2");
				ConstructorArgumentValues argumentValues = new ConstructorArgumentValues();
				argumentValues.addGenericArgumentValue(command, "String");
				beanDefinition.setConstructorArgumentValues(argumentValues);
				beanDefinition.setFactoryBeanName("testeW3");
				beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_NAME);
				beanDefinition.setAutowireCandidate(true);
				String nomeReg = "teste" + (int) (Math.random() * Math.random() * 1000000);
				registry.registerBeanDefinition(nomeReg, beanDefinition);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Object createWebServiceDynamicAdapt(String name, String nomeEP) {
		SensorType accountType = new SensorType();
//		SensorPropertyType spt = new SensorPropertyType();
//		spt.setName(name);

		AdapterFactory af = AdapterFactory.getInstance("AnnotationMapping.json");

		Map<String, String[]> nomeDoServico = new HashMap<String, String[]>();
		String[] nomeEndPoint = new String[1];
		nomeEndPoint[0] = nomeEP;
		nomeDoServico.put("value", nomeEndPoint);
		Map<String, Object> propriedades = new HashMap<>();
		propriedades.put("requestmapping", nomeDoServico);

		try {
			AdapterEntityType entityType = AdapterEntityType.getAdapter(accountType);

			Map<String, Object> allProperties = getAllKnownProperties(name);
			addOperation(allProperties, entityType);

			IPropertyType dataNascPropertyType = new GenericPropertyType("dataFabricacao", String.class);
			IPropertyType restcontrollerType = new GenericPropertyType("restcontroller", Boolean.class);
			accountType.getMetadatas().add(new MetadatasAccountType("restcontroller", Boolean.TRUE));

			entityType.setOperationProperties(propriedades);
			entityType.addPropertyType(dataNascPropertyType);
			entityType.addPropertyType(restcontrollerType);

			IEntity iEntity = entityType.createNewEntity();

			setData(iEntity, allProperties, "dataFabricacao");
			Object executeOperation = iEntity.executeOperation("ruleName");
			Object exemploJavaBean = af.generateAdapted(iEntity);
			System.out.println("resultado " + executeOperation);
			return exemploJavaBean;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addOperation(Map<String, Object> props, IEntityType tipoProduto) throws Exception {
		Set<String> keySet = props.keySet();
		for (String key : keySet) {
			if (key.contains("@")) {
				String partes[] = key.split("@");
				System.out.println(" chave: " + partes[0] + " regra: " + partes[1]);
				String ruleObject = "org.esfinge.aom.model.impl." + partes[1];
				Class[] type = { String.class };
				Class classDefinition = Class.forName(ruleObject);
				Constructor cons = classDefinition.getConstructor(type);
				Object[] obj = new Object[1];
				obj[0] = partes[0];
				Object ruleObjectInstance = cons.newInstance(obj);
				tipoProduto.addOperation("ruleName", (RuleObject) ruleObjectInstance);
			}
		}
	}

	public void setData(IEntity entity, Map<String, Object> props, String chave) {
		Set<String> keySet = props.keySet();

		for (String key : keySet) {
			if (key.contains(chave)) {
				Object value = props.get(key);
				try {
					entity.setProperty(chave, value);
				} catch (EsfingeAOMException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Object handleResults2(String command) {
		System.out.println("@@@@@@@@@@@@@@@ argumento: " + command);
		Object exercicio1 = mapObjects.get(command);
		// Map<String, Object> allProperties = getAllKnownProperties(command);
		// Object exercicio1 = createWebServiceDynamic(command, allProperties);
		return exercicio1;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Object> getAllKnownProperties(String wildCard) {
		Map<String, Object> rtn = new HashMap<>();
		if (env instanceof ConfigurableEnvironment) {
			for (PropertySource<?> propertySource : ((ConfigurableEnvironment) env).getPropertySources()) {
				if (propertySource instanceof EnumerablePropertySource) {
					for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
						if (key.contains(wildCard)) {
							Object value = propertySource.getProperty(key);
							key = getLastPeriodOfKey(key);
							rtn.put(key, value);
						}
					}
				}
			}
		}
		return rtn;
	}

	@SuppressWarnings("rawtypes")
	public List<String> getEndPointName(String name) {
		List<String> results = new ArrayList<>();
		String result = "";
		if (env instanceof ConfigurableEnvironment) {
			for (PropertySource<?> propertySource : ((ConfigurableEnvironment) env).getPropertySources()) {
				if (propertySource instanceof EnumerablePropertySource) {
					for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
						if (key.contains(name)) {
							Object value = propertySource.getProperty(key);
							result = (String) value;
							results.add(result);
						}
					}
				}
			}
		}
		return results;
	}

	private String getLastPeriodOfKey(String key) {
		String keyResult = key;
		int lastIndexOf = key.lastIndexOf(".");
		if (lastIndexOf > 0) {
			keyResult = key.substring(lastIndexOf + 1);
		}
		return keyResult;
	}



	@Override
	public void setEnvironment(Environment environment) {
		this.env = environment;
	}

}
