package org.limmen.docgen.app;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.limmen.docgen.domain.AsciiDocConverter;
import org.limmen.docgen.domain.FileSystemHelper;

public class FileFinderVisitor implements FileVisitor<Path> {

  private List<Path> files = new ArrayList<>();
  
  private AsciiDocConverter asciiDocConverter;

  private FileSystemHelper fileSystemHelper;

  public FileFinderVisitor(FileSystemHelper fileSystemHelper, AsciiDocConverter asciiDocConverter) {
    this.fileSystemHelper = fileSystemHelper;
    this.asciiDocConverter = asciiDocConverter;
  }

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    String extention = this.fileSystemHelper.getExtention(file);
    if (asciiDocConverter.canConvertFile(extention)) {
      this.files.add(file);
    }
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
    return FileVisitResult.CONTINUE;
  }

  public List<Path> getFiles() {
    return files;
  }
}
