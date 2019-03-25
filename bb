<?xml version="1.0" encoding="UTF-8" ?>
<project name="MsFileConverter" basedir=".">

    <property name="lib.dir" value="lib"/>

    <path id="classpath">
        <fileset dir="${lib.dir}" includes="**/*.jar"/>
    </path>

    <target name="compile">
        <mkdir dir="classes" />
        <javac srcdir="src" destdir="classes" classpathref="classpath" />
    </target>

    <target name="jar" depends="compile">
        <mkdir dir="jar" />
        <jar destfile="jar/msFileConverter.jar" basedir="classes">
            <fileset file="res/logback.xml" />
            <manifest></manifest>
        </jar>
    </target>

	<target name="one_jar" depends="jar">
		<zip destfile="Faraday-MsfileConverter-0.1.0-20190326.jar">
			<zipgroupfileset dir="lib" includes="*.jar" />
            <zipgroupfileset dir="jar" includes="*.jar" />
		</zip>
	</target>
</project>
