import { Component, OnInit, Input } from '@angular/core'
import { NsPlaylist } from '@ws-widget/collection'
import { ActivatedRoute } from '@angular/router'

@Component({
  selector: 'ws-app-playlist-card',
  templateUrl: './playlist-card.component.html',
  styleUrls: ['./playlist-card.component.scss'],
})
export class PlaylistCardComponent implements OnInit {

  @Input()
  playlist: NsPlaylist.IPlaylist | null = null

  defaultThumbnail = ''

  constructor(private route: ActivatedRoute) {
    if (this.route.snapshot.data.pageData.data) {
      this.defaultThumbnail = this.route.snapshot.data.pageData.data.defaultThumbnail
    }
  }

  ngOnInit() {
  }

}
