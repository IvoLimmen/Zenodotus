package org.limmen.docgen.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.limmen.docgen.converter.AsciiDocConverterImpl;
import org.limmen.docgen.domain.FileSystemHelper;
import org.limmen.docgen.domain.Indexer;
import org.limmen.docgen.indexer.IndexerImpl;
import org.limmen.docgen.model.Config;
import org.limmen.docgen.model.helper.Json;

public class Main {

  private FileSystemHelper fileSystemHelper;
  private Indexer indexer;
  private Config config;

  private Main() throws IOException {

    this.config = Json.load(Path.of(System.getProperty("user.dir"), "config", "docgen.json"));
    this.fileSystemHelper = new FileSystemHelper(config);
    this.indexer = new IndexerImpl(config, fileSystemHelper);

    this.walkThroughFiles();
  }

  public static void main(String[] args) throws IOException {
    new Main();
  }

  private void walkThroughFiles() throws IOException {
    var converter = new AsciiDocConverterImpl(fileSystemHelper);
    var supportFileVisitor = new SupportFileVisitor(fileSystemHelper);
    var asciiDocfileVisitor = new AsciiDocFileVisitor(converter, indexer, fileSystemHelper);

    Files.walkFileTree(config.getSourceDirectory(), asciiDocfileVisitor);
    Files.walkFileTree(config.getSourceDirectory(), supportFileVisitor);
    indexer.generate();
  }
}
