package org.limmen.docgen.indexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.limmen.docgen.domain.FileSystemHelper;
import org.limmen.docgen.model.Config;

public class IndexerImplTest {

  private IndexerImpl subject;

  @BeforeEach
  void init() {
    var config = new Config();
    config.setSourceDirectory("");
    config.setTargetDirectory("/home/ivo/Downloads/output");
    config.setTemplateDirectory("/home/ivo/projects/os/docgen/config");
    var fileSystemHelper = new FileSystemHelper(config);

    subject = new IndexerImpl(config, fileSystemHelper);
  }

  @Test
  void pathsShouldBeRelative() throws IOException {
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team1/Product1/API/petstore.html"));
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team1/Product1/design.html"));
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team1/Product2/example1.html"));
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team2/example2.html"));
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team2/example3.html"));

    var list = subject.getFiles();

    assertEquals(5, list.size());
    
    var path = list.iterator().next();

    assertTrue(path.startsWith("/Team1"));    
  }  

  @Test
  void returnsListsOfDirectories() throws IOException {
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team1/Product1/API/petstore.html"));
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team1/Product1/design.html"));
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team1/Product2/example1.html"));
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team2/example2.html"));
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team2/example3.html"));

    var directories = subject.getFoldersIndex(0);

    assertEquals(2, directories.size());

    assertEquals("Team1", directories.get(0));

    assertEquals("Team2", directories.get(1));
  }  

  @Test
  void returnsListsOfDirectoriesPart2() throws IOException {
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team1/Product1/API/petstore.html"));
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team1/Product1/design.html"));
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team1/Product2/example1.html"));
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team2/example2.html"));
    subject.addNewLink(Path.of("/home/ivo/Downloads/output/Team2/example3.html"));

    var directories = subject.getFoldersIndex(1);

    assertEquals(2, directories.size());

    assertEquals("Product1", directories.get(0));

    assertEquals("Product2", directories.get(1));
  }  
}
