package sync;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SyncDir {

	public static void main(String[] args) throws Exception {
		
		if (args.length != 2)
			throw new IllegalArgumentException("Invalid number of arguments;");

		var src = validate(args[0]);

		var dst = validate(args[1]);

		sync(src, dst);

	}

	private static File validate(String fileName) {
		var file = Paths.get(fileName).toFile();

		if (!file.exists()) {
			throw new IllegalArgumentException("File" + file.getPath() + " doesn't exists");
		}
		
		if (!file.isDirectory()) {
			throw new IllegalArgumentException("File" + file.getPath() + " is not a directory");

		}
		return file;
	}

	private static void sync(File src, File dst) {
		
		System.out.println("Start sync the folder " + dst.getAbsolutePath() + " with " + src.getAbsolutePath());
		
		var srcFiles = Stream.of(src.list()).collect(Collectors.toList());

		var dstFiles = Stream.of(dst.list()).collect(Collectors.toList());

		var add = new ArrayList<>(srcFiles);
		
		add.removeAll(dstFiles);

		add.stream().forEach(p -> copy(Paths.get(src.toURI()).resolve(p), Paths.get(dst.toURI()).resolve(p)));

		var remove = new ArrayList<>(dstFiles);
		
		remove.removeAll(srcFiles);

		remove.stream().map(p -> Paths.get(dst.toURI()).resolve(p)).forEach(SyncDir::delete);		
	}

	private static void delete(Path path) {
		
		try {
			System.out.print("File: " + path.toString());
			Files.delete(path);
			System.out.println(" - deleted");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void copy(Path src, Path dst) {
	
		try {
			if (src.toFile().isFile()) {
				System.out.print("File: " + src.toString());
				Files.copy(src, dst);
				System.out.println(" copied to " + dst);
			} else if(src.toFile().exists() && src.toFile().isDirectory() && src.toFile().list().length > 0 ){
				sync(src.toFile(), dst.toFile());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
