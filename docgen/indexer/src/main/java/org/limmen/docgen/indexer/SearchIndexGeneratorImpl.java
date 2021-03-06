package org.limmen.docgen.indexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.language.Soundex;
import org.limmen.docgen.domain.FileSystemHelper;
import org.limmen.docgen.domain.index.IndexItem;
import org.limmen.docgen.domain.index.IndexLink;
import org.limmen.docgen.domain.index.IndexNode;
import org.limmen.docgen.domain.index.SearchIndexGenerator;
import org.limmen.docgen.model.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchIndexGeneratorImpl implements SearchIndexGenerator {

  private final static Logger log = LoggerFactory.getLogger(SearchIndexGeneratorImpl.class);
  
  private Soundex soundex = new Soundex();
  private Map<String, List<IndexItem>> indexes = new HashMap<>();
  private Tokenizer tokenizer;
  private Config config;
  private FileSystemHelper fileSystemHelper;

  public SearchIndexGeneratorImpl(Config config, FileSystemHelper fileSystemHelper, Tokenizer tokenizer) {
    this.fileSystemHelper = fileSystemHelper;
    this.config = config;
    this.tokenizer = tokenizer;
  }

  @Override
  public void addIndexNode(IndexNode indexNode) {
    if (!this.config.getIndexGenerator().isIncludeSearch()) {
      return;
    } 

    var keywords = tokenizer.tokenize(indexNode.getRawText());
    
    List<IndexItem> indexItems = keywords.stream()
        .distinct()
        .filter(k -> k != null && k.length() > 0)
        .map(k -> createIndexItem(k, indexNode.getLinkPart(), indexNode.getSectionName(), indexNode.getTargetFile()))
        .toList();
        
    indexItems.forEach(indexItem -> {
      String key = indexItem.getKeyword().substring(0, 1);
      if (indexes.containsKey(key)) {
        List<IndexItem> list = indexes.get(key);
        
        if (list.contains(indexItem)) {
          list.get(list.indexOf(indexItem)).getLinks().addAll(indexItem.getLinks());
        } else {
          list.add(indexItem);
        }
      } else {
        List<IndexItem> list = new ArrayList<>();
        list.add(indexItem);
        indexes.put(key, list);
      }
    });    
  }

  private IndexItem createIndexItem(String keyword, String link, String section, Path targetFile) {
    return IndexItem.builder()
        .addLinks(IndexLink.builder()
            .link(link)
            .sectionName(section)
            .targetFile(fileSystemHelper.toTargetPathFromRoot(targetFile).toString())
            .build())
        .keyword(keyword)
        .soundex(soundex.encode(keyword))
        .build();
  }

  @Override
  public void generate() throws IOException {
    if (this.config.getIndexGenerator().isIncludeSearch()) {
      log.info("Generating index files for searching");
      ObjectMapper objectMapper = new ObjectMapper();
      for (var entry : getIndexes().entrySet()) {
        var file = Path.of(this.config.getTargetDirectory().toString(), "data", entry.getKey() + ".json");
        log.debug("Writing index file {}", file.toString());
        Files.createDirectories(file.getParent());
        Files.writeString(file, objectMapper.writeValueAsString(entry.getValue()));
      }
    } 
  }
  
  public Map<String, List<IndexItem>> getIndexes() {
    return indexes;
  }
}
