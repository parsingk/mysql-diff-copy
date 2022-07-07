package com.lib.dbdiffcopy;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class DiffCopyConfig {

    private final DatabaseDiffer differ;

    public DiffCopyConfig() {
        this.differ = new DatabaseDiffer();
    }

    public Builder builder() {
        return new Builder();
    }

    public class Builder {

        public Builder setSource(String sourceDataBaseName, NamedParameterJdbcTemplate source) {
            differ.setSource(sourceDataBaseName, source);
            return this;
        }

        public Builder setDestination(String destinationDataBaseName, NamedParameterJdbcTemplate destination) {
            differ.setDestination(destinationDataBaseName, destination);
            return this;
        }

        public Builder setExcludeRoutines(List<String> excludeRoutines) {
            differ.setExcludeRoutines(excludeRoutines);
            return this;
        }

        public DatabaseDiffer build() {
            return differ;
        }
    }
}
