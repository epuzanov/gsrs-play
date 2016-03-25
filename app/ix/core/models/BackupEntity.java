package ix.core.models;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ix.core.controllers.EntityFactory.EntityMapper;
import ix.utils.Util;

@Entity
@Table(name="ix_core_backup")
public class BackupEntity extends IxModel{
	
	@Id
	private Long id;
	
	@Column(unique = true)
	private String refid;
	private String kind;
	
	@Lob
    @JsonIgnore
    @Indexable(indexed=false)
    @Basic(fetch=FetchType.EAGER)
    public byte[] data;
//	@Lob
//	private String json;
	
	private String sha1;
	
	private boolean compressed=true;
	
	public BackupEntity(){
		
	}
	public BackupEntity(boolean compressed){
		this.compressed=compressed;
	}
	
	public Class<?> getKind(){
		try {
			return Class.forName(kind);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	private InputStream asStream() throws Exception{
		InputStream stream = new ByteArrayInputStream(getBytes());
		return stream;
	}
	
	@JsonIgnore
	private byte[] getBytes() throws Exception{
		if(compressed){
			return Util.decompress(data);
		}else{
			return data;
		}
	}
	
	@JsonIgnore
	private void setBytes(byte[] data) throws Exception{
		if(compressed){
			this.data= Util.compress(data);
		}else{
			this.data=data;
		}
	}
	
	
	@JsonIgnore
	public Object getInstantiated() throws Exception{
		Class<?> cls= getKind();
		if(cls==null){
			throw new IllegalStateException("Kind is not set for object");
		}
		EntityMapper em = EntityMapper.FULL_ENTITY_MAPPER();
		Object inst=em.readValue(asStream(), cls);
		return inst;
	}
	@JsonIgnore
	public void setInstantiated(BaseModel o) throws Exception{
		kind=o.getClass().getName();
		refid=o.fetchIdAsString();
		EntityMapper em = EntityMapper.FULL_ENTITY_MAPPER();
		setBytes(em.toJson(o).getBytes(StandardCharsets.UTF_8));
		sha1=Util.sha1(data);
	}
	
	public boolean matchesHash(){
		String sha1=Util.sha1(data);
		return this.sha1.equals(sha1);
	}
}
