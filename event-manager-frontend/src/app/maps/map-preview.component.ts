import { CommonModule } from '@angular/common';
import { Component, Input, OnChanges, SimpleChanges, inject, signal } from '@angular/core';
import { GoogleMap, MapMarker } from '@angular/google-maps';

import { MapsLoaderService } from './maps-loader.service';

@Component({
  selector: 'app-map-preview',
  standalone: true,
  imports: [CommonModule, GoogleMap, MapMarker],
  templateUrl: './map-preview.component.html',
  styleUrl: './map-preview.component.css'
})
export class MapPreviewComponent implements OnChanges {
  @Input() latitude: number | null = null;
  @Input() longitude: number | null = null;
  @Input() zoom = 13;

  readonly apiReady = signal(false);
  center: google.maps.LatLngLiteral | null = null;
  markerPosition: google.maps.LatLngLiteral | null = null;

  private readonly mapsLoader = inject(MapsLoaderService);
  private loadingPromise?: Promise<void>;

  constructor() {
    this.loadingPromise = this.loadMaps();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['latitude'] || changes['longitude']) {
      this.updateCoordinates();
    }
  }

  private async loadMaps(): Promise<void> {
    try {
      await this.mapsLoader.load();
      this.apiReady.set(true);
      this.updateCoordinates();
    } catch {
      this.apiReady.set(false);
    }
  }

  private updateCoordinates(): void {
    if (!this.apiReady()) {
      return;
    }
    if (this.latitude == null || this.longitude == null) {
      this.center = null;
      this.markerPosition = null;
      return;
    }
    const coords: google.maps.LatLngLiteral = {
      lat: this.latitude,
      lng: this.longitude
    };
    this.center = coords;
    this.markerPosition = coords;
  }
}
