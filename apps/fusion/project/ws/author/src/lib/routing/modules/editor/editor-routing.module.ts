import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { InitResolver } from '@ws/author/src/lib/services/init-resolve.service'
import { EditorComponent } from './components/editor/editor.component'
import { CreateContentResolverService } from './services/create-content-resolver.service'

const routes: Routes = [
  {
    path: '',
    component: EditorComponent,
    children: [
      {
        path: 'curate',
        loadChildren: () =>
          import('./routing/modules/curate/curate.module').then(u => u.CurateModule),
      },
      {
        path: 'kartifact-pa',
        loadChildren: () =>
          import('./routing/modules/knowledge-artifact-pa/knowledge-artifact-pa.module').then(
            u => u.KnowledgeArtifactPaModule,
          ),
        resolve: { content: CreateContentResolverService },
      },
      {
        path: 'channel',
        loadChildren: () =>
          import('./routing/modules/channel/channel.module').then(u => u.ChannelModule),
      },
      {
        path: 'upload',
        loadChildren: () =>
          import('./routing/modules/upload/upload.module').then(u => u.UploadModule),
      },
      {
        path: 'collection',
        data: {
          load: ['collection', 'create'],
        },
        resolve: {
          script: InitResolver,
        },
        loadChildren: () =>
          import('./routing/modules/collection/collection.module').then(u => u.CollectionModule),
      },
      {
        path: 'quiz',
        loadChildren: () => import('./routing/modules/quiz/quiz.module').then(u => u.QuizModule),
      },
      {
        path: 'assessment',
        loadChildren: () => import('./routing/modules/quiz/quiz.module').then(u => u.QuizModule),
      },
      {
        path: 'web-module',
        loadChildren: () => import('./routing/modules/web-page/web-page.module').then(u => u.WebPageModule),
      },
      {
        path: 'class-diagram',
        loadChildren: () => import('./routing/modules/class-diagram/class-diagram.module').then(u => u.ClassDiagramModule),
      },
      {
        path: 'knowledge-board',
        loadChildren: () =>
          import('./routing/modules/knowledge-board/knowledge-board.module').then(
            u => u.KnowledgeBoardModule,
          ),
      },
      {
        path: 'iap-assessment',
        loadChildren: () =>
          import('./routing/modules/iap-assessment/iap-assessment.module').then(
            u => u.IapAssessmentModule,
          ),
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EditorRoutingModule { }
