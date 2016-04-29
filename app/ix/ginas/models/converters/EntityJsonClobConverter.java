package ix.ginas.models.converters;

import java.io.IOException;

import ix.core.Converter;
import ix.core.controllers.EntityFactory;
import ix.core.controllers.EntityFactory.EntityMapper;

public abstract class EntityJsonClobConverter<K> extends EntityClobConverter<K> {
	public EntityMapper em =  EntityFactory.EntityMapper.FULL_ENTITY_MAPPER();
	private Class<K> cls;
	
	public EntityJsonClobConverter(Class<K> cls) {
		super(cls);
		this.cls=cls;
	}

	@Override
	public String convertToString(K value) throws IOException {
		return em.toJson(value);
	}

	@Override
	public K convertFromString(String bytes) throws IOException {
		if(bytes==null)return null;
		return em.readValue(bytes, cls);
	}
}