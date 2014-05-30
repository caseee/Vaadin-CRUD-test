package it.gigalol.vaadinapp.data;

import it.gigalol.vaadinapp.sql.LinkedTable;

import java.util.Locale;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class ConverterFactory <Model,Presentation> implements Converter <Model,Presentation> {

	private LinkedTable lt;
	
	private static final long serialVersionUID = 4788068105649656695L;

//	public ConverterFactory <Model,Presentation> createIstance(LinkedTable lt) {
//		return createIstance (lt.getIdType().getClass(),lt.getShowType().getClass(), lt );
//	}
	
	public static <IModel,IPresentation> ConverterFactory <IModel,IPresentation> createIstance(LinkedTable lt) {
		return new ConverterFactory<IModel, IPresentation>(lt);
	}
	
	private ConverterFactory(LinkedTable lt) {
		this.lt=lt;
	}
	
	@Override
	public Presentation convertToModel(Model value,
			Class<? extends Presentation> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return null;
	}

	@Override
	public Model convertToPresentation(Presentation value,
			Class<? extends Model> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<Presentation> getModelType() {
				return (Class<Presentation>) lt.getShowType();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<Model> getPresentationType() {
		return (Class<Model>) lt.getIdType();
	}


	
}
