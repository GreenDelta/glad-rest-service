package com.greendelta.search.glad.rest.model;

import static com.greendelta.search.glad.rest.model.IndexType.BOOLEAN;
import static com.greendelta.search.glad.rest.model.IndexType.DATE;
import static com.greendelta.search.glad.rest.model.IndexType.DOUBLE;
import static com.greendelta.search.glad.rest.model.IndexType.INTEGER;
import static com.greendelta.search.glad.rest.model.IndexType.KEYWORD;
import static com.greendelta.search.glad.rest.model.IndexType.TEXT;

import com.greendelta.search.glad.rest.model.Types.AggregationType;
import com.greendelta.search.glad.rest.model.Types.BiogenicCarbonModeling;
import com.greendelta.search.glad.rest.model.Types.CarbonStorageModeling;
import com.greendelta.search.glad.rest.model.Types.EmissionModeling;
import com.greendelta.search.glad.rest.model.Types.EndOfLifeModeling;
import com.greendelta.search.glad.rest.model.Types.Format;
import com.greendelta.search.glad.rest.model.Types.InfrastructureModeling;
import com.greendelta.search.glad.rest.model.Types.ModelingType;
import com.greendelta.search.glad.rest.model.Types.MultifunctionalModeling;
import com.greendelta.search.glad.rest.model.Types.ProcessType;
import com.greendelta.search.glad.rest.model.Types.RepresentativenessType;
import com.greendelta.search.glad.rest.model.Types.ReviewSystem;
import com.greendelta.search.glad.rest.model.Types.ReviewType;
import com.greendelta.search.glad.rest.model.Types.SourceReliability;
import com.greendelta.search.glad.rest.model.Types.WaterModeling;

public class Index {

	@IndexField(type = TEXT, required = true)
	public String name;
	@IndexField(type = TEXT)
	public String category;
	@IndexField(type = TEXT)
	public String description;
	@IndexField(type = TEXT)
	public String technology;

	@IndexField(type = KEYWORD, required = true)
	public String refId;
	@IndexField(type = KEYWORD, required = true)
	public String dataSetUrl;
	@IndexField(type = KEYWORD, required = true, aggregatable = true)
	public Format format;
	@IndexField(type = KEYWORD, aggregatable = true)
	public String location;
	@IndexField(type = KEYWORD, aggregatable = true)
	public String dataprovider;
	@IndexField(type = KEYWORD, aggregatable = true)
	public String[] categoryPaths;
	@IndexField(type = KEYWORD, aggregatable = true)
	public String[] supportedNomenclatures;
	@IndexField(type = KEYWORD, aggregatable = true)
	public String[] lciaMethods;
	@IndexField(type = KEYWORD, aggregatable = true)
	public String[] unspscPaths;
	@IndexField(type = KEYWORD, aggregatable = true)
	public String[] co2pePaths;
	@IndexField(type = KEYWORD, aggregatable = true, defaultValue = "UNKNOWN")
	public ProcessType processType;
	@IndexField(type = KEYWORD, aggregatable = true, defaultValue = "EXPERT_BASED")
	public RepresentativenessType representativenessType;
	@IndexField(type = KEYWORD, aggregatable = true, defaultValue = "UNKNOWN")
	public ModelingType modelingType;
	@IndexField(type = KEYWORD, aggregatable = true, defaultValue = "NOT_APPLICABLE")
	public MultifunctionalModeling multifunctionalModeling;
	@IndexField(type = KEYWORD, aggregatable = true, defaultValue = "NOT_APPLICABLE")
	public BiogenicCarbonModeling biogenicCarbonModeling;
	@IndexField(type = KEYWORD, aggregatable = true, defaultValue = "NOT_APPLICABLE")
	public EndOfLifeModeling endOfLifeModeling;
	@IndexField(type = KEYWORD, aggregatable = true, defaultValue = "NOT_APPLICABLE")
	public WaterModeling waterModeling;
	@IndexField(type = KEYWORD, aggregatable = true, defaultValue = "NOT_APPLICABLE")
	public InfrastructureModeling infrastructureModeling;
	@IndexField(type = KEYWORD, aggregatable = true, defaultValue = "NOT_APPLICABLE")
	public EmissionModeling emissionModeling;
	@IndexField(type = KEYWORD, aggregatable = true, defaultValue = "NOT_APPLICABLE")
	public CarbonStorageModeling carbonStorageModeling;
	@IndexField(type = KEYWORD, aggregatable = true, defaultValue = "ESTIMATED_UNQUALIFIED")
	public SourceReliability sourceReliability;
	@IndexField(type = KEYWORD, aggregatable = true, defaultValue = "NOT_APPLICABLE")
	public AggregationType aggregationType;
	@IndexField(type = KEYWORD, aggregatable = true, defaultValue = "NONE")
	public ReviewType reviewType;
	@IndexField(type = KEYWORD, aggregatable = true, defaultValue = "NOT_APPLICABLE")
	public ReviewSystem reviewSystem;
	@IndexField(type = KEYWORD)
	public String unspscCode;
	@IndexField(type = KEYWORD)
	public String co2peCode;
	@IndexField(type = KEYWORD)
	public String copyrightHolder;
	@IndexField(type = KEYWORD)
	public String license;
	@IndexField(type = KEYWORD)
	public String contact;
	@IndexField(type = KEYWORD)
	public String[] categories;
	@IndexField(type = KEYWORD)
	public String[] reviewers;

	@IndexField(type = DATE)
	public Long validFrom;
	@IndexField(type = DATE)
	public Long validUntil;

	@IndexField(type = INTEGER, aggregatable = true)
	public Integer validFromYear;
	@IndexField(type = INTEGER, aggregatable = true)
	public Integer validUntilYear;

	@IndexField(type = DOUBLE)
	public Double latitude;
	@IndexField(type = DOUBLE)
	public Double longitude;
	@IndexField(type = DOUBLE, defaultValue = "100.0")
	public Double completeness;
	@IndexField(type = DOUBLE, defaultValue = "0.0")
	public Double amountDeviation;
	@IndexField(type = DOUBLE, defaultValue = "1.7976931348623157E308")
	public Double representativenessValue;

	@IndexField(type = BOOLEAN, aggregatable = true)
	public boolean copyrightProtected;
	@IndexField(type = BOOLEAN, aggregatable = true)
	public boolean free;
	@IndexField(type = BOOLEAN, aggregatable = true)
	public boolean publiclyAccessible;

}
