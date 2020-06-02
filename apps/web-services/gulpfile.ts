import del from 'del'
import gulp from 'gulp'
import gulpTypeScript from 'gulp-typescript'
import sonarqubeScanner from 'sonarqube-scanner'

import { Gulpclass, SequenceTask, Task } from 'gulpclass'

const project = gulpTypeScript.createProject('tsconfig.json')
const dist = './dist'

@Gulpclass()
export class Gulpfile {
  @Task('scan')
  scan(cb: Function) {
    sonarqubeScanner(
      {
        options: {},
        serverUrl: 'http://10.177.157.45:3000/',
        token: '8cfe2cd380b8a8d908f0dc4a179469e27920eae1',
      },
      cb
    )
  }
  @Task('del-dist')
  clean() {
    return del('./dist/**')
  }

  @Task('compile-project')
  compileProject() {
    const tsResult = gulp.src('src/**/*.ts').pipe(project())
    return tsResult.js.pipe(gulp.dest(dist))
  }

  @Task('copy-package')
  copyPackageJson() {
    return gulp.src('./package.json').pipe(gulp.dest(dist))
  }

  @Task('copy-json')
  copyJson() {
    return gulp.src('src/**/*.json').pipe(gulp.dest(dist))
  }

  @Task('copy-assets')
  copyAssets() {
    return gulp.src('src/assets/**/*').pipe(gulp.dest(`${dist}/assets`))
  }

  @SequenceTask('build')
  build() {
    return [
      'del-dist',
      'compile-project',
      'copy-package',
      'copy-json',
      'copy-assets',
    ]
  }
}
