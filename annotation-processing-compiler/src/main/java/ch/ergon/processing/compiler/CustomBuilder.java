package ch.ergon.processing.compiler;

import com.squareup.javapoet.JavaFile;

import javax.annotation.processing.Filer;

public abstract class CustomBuilder<T> {

	private final Filer filer;

	public CustomBuilder(Filer filer) {
		this.filer = filer;
	}

	protected abstract JavaFile generateCode(T t) throws Exception;

	protected abstract Iterable<T> iterable();

	public final void generate() {
		try {
			for (T t : iterable()) {
				JavaFile javaFile = generateCode(t);
				javaFile.writeTo(filer);
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception while generating source code", e);
		}
	}

}
