package jcf.cmd.runner.spring;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.asm.ClassReader;
import org.springframework.core.io.Resource;
import org.springframework.core.type.ClassMetadata;

class SimpleMetadataReader {

	private final Resource resource;
	private final ClassMetadata classMetadata;

	SimpleMetadataReader(Resource resource, ClassLoader classLoader) throws IOException {
		InputStream is = resource.getInputStream();
		ClassReader classReader = null;
		try {
			classReader = new ClassReader(is);
		} finally {
			is.close();
		}

		ClassMetadataReadingVisitor visitor = new ClassMetadataReadingVisitor();
		classReader.accept(visitor, true);
		
		// (since AnnotationMetadataReader extends ClassMetadataReadingVisitor)
		this.classMetadata = visitor;
		this.resource = resource;
	}

	public Resource getResource() {
		return this.resource;
	}

	public ClassMetadata getClassMetadata() {
		return this.classMetadata;
	}

}