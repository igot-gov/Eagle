import { Pipe, PipeTransform } from '@angular/core'
import { NsPlaylist } from '@ws-widget/collection'

@Pipe({
  name: 'filterPlaylist',
})
export class FilterPlaylistPipe implements PipeTransform {
  transform(playlists: any): NsPlaylist.IPlaylist[] | undefined {
    const filteredPlaylists = playlists.result.content

    return filteredPlaylists ? filteredPlaylists : undefined
  }
}
