package hello;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.esfinge.aom.api.model.IEntity;
import org.esfinge.aom.api.model.IEntityType;
import org.esfinge.aom.api.model.IPropertyType;
import org.esfinge.aom.model.dynamic.factory.AdapterFactory;
import org.esfinge.aom.model.impl.CalculaAnos;
import org.esfinge.aom.model.impl.GenericEntityType;
import org.esfinge.aom.model.impl.GenericPropertyType;
import org.springframework.beans.factory.config.AbstractFactoryBean;

//@Configuration(value = "factory2")
public class DynamicFactoryBean extends AbstractFactoryBean<Object> {

	@Override
	public Class<?> getObjectType() {
		return Object.class;
	}

	// @Bean(name="testeWr")
	// public Object testeWr() {
	// System.out.println("---------------- criando o bean dinamico");
	// try {
	// return createInstance();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	public Object createDynamicObject() {
		return criaObjetoDinamico();
	}

	@Override
	protected Object createInstance() throws Exception {
		Object exemploJavaBean = null;

		try {
			exemploJavaBean = criaObjetoDinamico();

		//	ObjectPrinter.printClass(exemploJavaBean);

			try {
				Method declaredMethod = exemploJavaBean.getClass().getDeclaredMethod("executeOperation", String.class,
						Object[].class);
				Object result = declaredMethod.invoke(exemploJavaBean, "anosFabricacao", null);
				System.out.println(result);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return exemploJavaBean;
	}

	private Object criaObjetoDinamico() {
		Object exemploJavaBean = null;
		System.out.println("------------------------ Dynamic Factory ");
		try {
			AdapterFactory af = AdapterFactory.getInstance("AnnotationMapping.json");

			IEntityType tipoProduto = new GenericEntityType("ProdutoNovo3");

			// criando property types
			IPropertyType dataNascPropertyType = new GenericPropertyType("dataFabricacao", Date.class);
			IPropertyType nomePropertyType = new GenericPropertyType("nome", String.class);

			Map<String, String[]> nomeDoServico = new HashMap<String, String[]>();
			String[] nomeEndPoint = new String[1];
			nomeEndPoint[0] = "verdade3";
			nomeDoServico.put("value", nomeEndPoint);

			nomePropertyType.setProperty("requestmapping", nomeDoServico);

			// Map<String, Object> nomeDoServico = new HashMap<String,
			// Object>();
			// nomeDoServico.put("value", "/verdade2");
			// nomePropertyType.setProperty("requestmapping", nomeDoServico);

			// adicionando property types no tipo de entidade
			tipoProduto.addPropertyType(dataNascPropertyType);
			tipoProduto.addPropertyType(nomePropertyType);
			tipoProduto.addOperation("anosFabricacao", new CalculaAnos("dataFabricacao"));

			// Essas anotações devem ser adicionadas na rule
			// tipoProduto.setProperty("ruleClass", true);
			// nomePropertyType.setProperty("ruleObject", true);

			GregorianCalendar dataFabr = new GregorianCalendar();
			dataFabr.set(2010, 11, 23);
			Map<String, Object> parametersSubstring = new HashMap<String, Object>();
			parametersSubstring.put("dataFabricacao", "2016");
			parametersSubstring.put("perecivel", true);
			nomePropertyType.setProperty("ruleAttribute", parametersSubstring);

			tipoProduto.setProperty("restcontroller", true);

			IEntity produto = tipoProduto.createNewEntity();
			produto.setProperty("nome", "Notebook DELL");
			produto.setProperty("dataFabricacao", dataFabr.getTime());

			exemploJavaBean = af.generate(produto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exemploJavaBean;
	}

	public static Object createTesteWr() {
		try {
			System.out.println("@@@@@@@@@@@@@@@@@@ dynamic factory ");
			AdapterFactory af = AdapterFactory.getInstance("AnnotationMapping.json");

			IEntityType tipoProduto = new GenericEntityType("ProdutoNovo1");

			// criando property types
			IPropertyType dataNascPropertyType = new GenericPropertyType("dataFabricacao", Date.class);
			IPropertyType nomePropertyType = new GenericPropertyType("nome", String.class);

			Map<String, String[]> nomeDoServico = new HashMap<String, String[]>();
			String[] nomeEndPoint = new String[1];
			nomeEndPoint[0] = "verdade3";
			nomeDoServico.put("value", nomeEndPoint);

			nomePropertyType.setProperty("requestmapping", nomeDoServico);

			// adicionando property types no tipo de entidade
			tipoProduto.addPropertyType(dataNascPropertyType);
			tipoProduto.addPropertyType(nomePropertyType);
			tipoProduto.addOperation("anosFabricacao", new CalculaAnos("dataFabricacao"));

			// Essas anotações devem ser adicionadas na rule
			tipoProduto.setProperty("ruleClass", true);
			nomePropertyType.setProperty("ruleObject", true);

			GregorianCalendar dataFabr = new GregorianCalendar();
			dataFabr.set(2010, 11, 23);
			Map<String, Object> parametersSubstring = new HashMap<String, Object>();
			parametersSubstring.put("dataFabricacao", "2016");
			parametersSubstring.put("perecivel", true);
			nomePropertyType.setProperty("ruleAttribute", parametersSubstring);

			tipoProduto.setProperty("restcontroller", true);

			IEntity produto = tipoProduto.createNewEntity();
			produto.setProperty("nome", "Notebook DELL");
			produto.setProperty("dataFabricacao", dataFabr.getTime());

			return af.generate(produto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
