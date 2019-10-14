package io.xdoo.rad.model.repositories;

import io.xdoo.rad.model.beans.DayCounterEntry;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DayCounterEntryRepository extends ElasticsearchRepository<DayCounterEntry, String> {
}
