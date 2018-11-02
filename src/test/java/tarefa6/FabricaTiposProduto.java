package tarefa6;

import java.util.Date;

import org.esfinge.aom.api.model.IEntityType;
import org.esfinge.aom.exceptions.EsfingeAOMException;
import org.esfinge.aom.model.impl.GenericEntityType;
import org.esfinge.aom.model.impl.GenericPropertyType;

public class FabricaTiposProduto {

	public static IEntityType getTipoEntidadeCobranca() throws EsfingeAOMException{
		IEntityType produto = new GenericEntityType("Produto");
	
		//criando property types
		GenericPropertyType dataNascPropertyType = new GenericPropertyType("dataFabricacao", Date.class);
		dataNascPropertyType.setProperty("notempty", true);

		GenericPropertyType nomePropertyType = new GenericPropertyType("nome", String.class);
		nomePropertyType.setProperty("notempty", true);
		
		GenericPropertyType perecivel = new GenericPropertyType("perecivel", Boolean.class);
		perecivel.setProperty("notempty", true);
		
		GenericPropertyType validade = new GenericPropertyType("validade", Integer.class);
		validade.setProperty("range.min", 1);
		validade.setProperty("range.min", 730);
					
		//adicionando property types no tipo de entidade
		produto.addPropertyType(dataNascPropertyType);
		produto.addPropertyType(nomePropertyType);
		produto.addPropertyType(perecivel);
		produto.addPropertyType(validade);
		
		return produto;
	}
	
}
