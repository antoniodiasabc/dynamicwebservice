package hello;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.esfinge.aom.api.model.IEntity;
import org.esfinge.aom.api.model.IEntityType;
import org.esfinge.aom.api.model.IPropertyType;
import org.esfinge.aom.api.model.RuleObject;
import org.esfinge.aom.model.dynamic.factory.AdapterFactory;
import org.esfinge.aom.model.impl.CalculaAnos;
import org.esfinge.aom.model.impl.GenericEntityType;
import org.esfinge.aom.model.impl.GenericPropertyType;
import org.json.simple.JSONObject;
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

@Component(value = "testeW2")
public class MeuBeanRegistration2 implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

	private static final String NOMEENDPOINT = "nomeendpoint1";
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

		System.out.println("passou no postProcessBeanDefinitionRegistry do MeuBeanRegistration2 ");

		List<String> endPointNames = getEndPointName(NOMEENDPOINT);
		for (String nomeEndPoint : endPointNames) {
			if (nomeEndPoint != null) {
				String[] split = nomeEndPoint.split("/");
				if (split.length == 2) {
					command = split[0];
					nomeEP = "/" + split[1];
				}
			}

			Object exercicio1 = createWebServiceDynamic(command, nomeEP);
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
				beanDefinition.setFactoryBeanName("testeW2");
				beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_NAME);
				beanDefinition.setAutowireCandidate(true);
				String nomeReg = "teste" + (int) (Math.random() * Math.random() * 1000000);
				registry.registerBeanDefinition(nomeReg, beanDefinition);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Object createWebServiceDynamic(String command, String nomeEP) {
		Object exemploJavaBean = null;

		try {
			AdapterFactory af = AdapterFactory.getInstance("AnnotationMapping.json");

			IEntityType tipoProduto = new GenericEntityType(command);

			// criando property types
			IPropertyType dataNascPropertyType = new GenericPropertyType("dataFabricacao", Date.class);
	
			Map<String, String[]> nomeDoServico = new HashMap<String, String[]>();
			String[] nomeEndPoint = new String[1];
			nomeEndPoint[0] = nomeEP;
			nomeDoServico.put("value", nomeEndPoint);

			// adicionando property types no tipo de entidade
			tipoProduto.addPropertyType(dataNascPropertyType);
			tipoProduto.addOperation("anosFabricacao", new CalculaAnos("dataFabricacao"));
			
			Map<String, Object> propriedades = new HashMap<>();
			propriedades.put("requestmapping", nomeDoServico);
			tipoProduto.setOperationProperties(propriedades);

			GregorianCalendar dataFabr = new GregorianCalendar();
			dataFabr.set(2010, 11, 23);
			
			tipoProduto.setProperty("restcontroller", true);

			IEntity produto = tipoProduto.createNewEntity();
			produto.setProperty("dataFabricacao", dataFabr.getTime());
			exemploJavaBean = af.generate(produto);
			//ObjectPrinter.printClass(exemploJavaBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exemploJavaBean;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	private IEntity createPropertyAndSetValues(Map<String, Object> props, IEntityType tipoProduto) throws Exception {
		Set<String> keySet = props.keySet();
		for (String key : keySet) {
			GenericPropertyType propertyType = new GenericPropertyType(key, String.class);
			tipoProduto.addPropertyType(propertyType);

			if (key.contains("@")) {
				String partes[] = key.split("@");
				System.out.println(" chave: " + partes[0] + " regra: " + partes[1]);
				String ruleObject = "org.esfinge.aom.model.impl." + partes[1];
				Class[] type = { String.class };
				Class classDefinition = Class.forName(ruleObject);
				Constructor cons = classDefinition.getConstructor(type);
				Object[] obj = new Object[1];
				obj[0] = key;
				Object ruleObjectInstance = cons.newInstance(obj);
				tipoProduto.addOperation("ruleName", (RuleObject) ruleObjectInstance);	
			}
		}

		tipoProduto.setProperty("restcontroller", true);

		IEntity produto = tipoProduto.createNewEntity();
		for (String key : keySet) {
			Object value = props.get(key);
			produto.setProperty(key, value);
		}
		return produto;
	}

	public Object handleResults2(String command) {
		System.out.println("@@@@@@@@@@@@@@@ argumento: " + command);
		Map<String, Object> allProperties = getAllKnownProperties(command);
		Object exercicio1 = createWebServiceDynamic(command, allProperties);
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

	private Object createWebServiceDynamic(String command, Map<String, Object> props) {
		Object exemploJavaBean = null;

		try {
			AdapterFactory af = AdapterFactory.getInstance("AnnotationMapping.json");
			IEntityType tipoProduto = new GenericEntityType("hello", command);
			IEntity produto = createPropertyAndSetValues(props, tipoProduto);

			// EL
			if (produto.getProperty("formula") != null) {
				Map<String, Object> mapa = new HashMap<>();
				String expr = (String) produto.getProperty("formula").getValue();
				String vars[] = expr.split(" ");
				for (int i = 1; i < vars.length; i += 2) {
					String parte = vars[i];
					System.out.println("parte: " + parte);
					if (parte.equals("pi")) {
						mapa.put(parte, Math.PI);
					} else {
						mapa.put(parte, produto.getProperty(parte).getValue());
					}
				}

				Object resultEl = produto.executeEL(expr, produto.getClass(), mapa);
				System.out.println(resultEl);
				String resultJson = prettyPrint(props, resultEl);
				produto.setProperty("valido", resultJson);
			} else {
				Object resultOperation = "0";
				try {
					resultOperation = produto.executeOperation("ruleName");
				} catch (Exception e) {
					System.out.println("Erro: " + e);
				}
				//System.out.println("result: " + resultOperation);
				//String resultJson = prettyPrint(props, resultOperation);
				produto.setProperty("valido", resultOperation);
			}
			exemploJavaBean = af.generate(produto);			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return exemploJavaBean;

	}
	
	private String prettyPrint(Map<String, Object> props, Object resultOperation) {
		Set<Entry<String, Object>> entrySet = props.entrySet();

		Iterator<Entry<String, Object>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> next = iterator.next();
			String key = next.getKey();
			if (key.equals("valido")) {
				props.put(key, String.valueOf(resultOperation));
			} else if (key.contains("data")) {
				String value = (String) props.get(key);
				if (value.contains("/")) {
					String value2 = value.replaceAll("/", "-");
					next.setValue(value2);
				}
			}
		}
		return JSONObject.toJSONString(props).toString();
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.env = environment;
	}

}
