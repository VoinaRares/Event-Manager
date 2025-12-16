import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
  ViewChild,
  inject
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GoogleMap, MapMarker } from '@angular/google-maps';
import { MapsLoaderService } from './maps-loader.service';

export interface SelectedLocation {
  displayName: string;
  latitude: number;
  longitude: number;
  placeId?: string | null;
}

@Component({
  selector: 'app-location-picker',
  standalone: true,
  imports: [CommonModule, FormsModule, GoogleMap, MapMarker],
  templateUrl: './location-picker.component.html',
  styleUrl: './location-picker.component.css'
})
export class LocationPickerComponent implements AfterViewInit, OnDestroy {
  @Input() placeholder = 'Search for a venue or address';
  @Input() locationName = '';

  @Output() locationNameChange = new EventEmitter<string>();
  @Output() locationSelected = new EventEmitter<SelectedLocation>();
  @Output() locationCleared = new EventEmitter<void>();

  @ViewChild('searchInput') searchInput?: ElementRef<HTMLInputElement>;

  readonly defaultCenter: google.maps.LatLngLiteral = { lat: 51.1657, lng: 10.4515 }; // Center of Germany
  center: google.maps.LatLngLiteral = { ...this.defaultCenter };
  zoom = 5;
  markerPosition: google.maps.LatLngLiteral | null = null;
  apiReady = false;
  mapReady = false;
  authFailed = false;

  private readonly mapsLoader = inject(MapsLoaderService);
  private mapInstance?: google.maps.Map;
  private autocomplete?: google.maps.places.Autocomplete;
  private placeListener?: google.maps.MapsEventListener;

  async ngAfterViewInit(): Promise<void> {
    try {
      await this.mapsLoader.load();
      this.apiReady = true;
      this.setupAutocomplete();
    } catch {
      this.authFailed = true;
    }
  }

  ngOnDestroy(): void {
    this.placeListener?.remove?.();
  }

  onInputChange(value: string): void {
    this.locationName = value;
    this.locationNameChange.emit(value);

    // If the name changes manually, clear any stale coordinates.
    if (this.markerPosition) {
      this.clearSelection();
    }
  }

  onMapReady(map: google.maps.Map): void {
    this.mapInstance = map;
    this.mapReady = true;
    this.setupAutocomplete();
  }

  onAuthFailure(): void {
    this.authFailed = true;
  }

  onMapClick(event: google.maps.MapMouseEvent): void {
    if (!event.latLng) {
      return;
    }

    const coords = event.latLng.toJSON();
    const name = this.locationName?.trim() || 'Dropped pin';

    this.applySelection(
      {
        displayName: name,
        latitude: coords.lat,
        longitude: coords.lng,
        placeId: null
      },
      false
    );
  }

  clearSelection(): void {
    this.markerPosition = null;
    this.center = { ...this.defaultCenter };
    this.zoom = 5;
    this.locationCleared.emit();
  }

  private setupAutocomplete(): void {
    if (!this.apiReady || !this.searchInput?.nativeElement || !google.maps?.places) {
      return;
    }

    if (this.autocomplete) {
      return;
    }

    this.autocomplete = new google.maps.places.Autocomplete(this.searchInput.nativeElement, {
      fields: ['geometry', 'name', 'formatted_address', 'place_id'],
      types: ['establishment', 'geocode']
    });

    this.placeListener = this.autocomplete.addListener('place_changed', () => {
      const place = this.autocomplete?.getPlace();

      if (!place?.geometry?.location) {
        return;
      }

      const location = place.geometry.location.toJSON();
      const displayName = place.name || place.formatted_address || this.locationName || 'Selected place';

      this.applySelection(
        {
          displayName,
          latitude: location.lat,
          longitude: location.lng,
          placeId: place.place_id ?? null
        },
        true
      );
    });
  }

  private applySelection(selection: SelectedLocation, updateName: boolean): void {
    this.markerPosition = { lat: selection.latitude, lng: selection.longitude };
    this.center = { ...this.markerPosition };
    this.zoom = 14;

    if (updateName) {
      this.locationName = selection.displayName;
      this.locationNameChange.emit(selection.displayName);
    }

    this.locationSelected.emit(selection);
    this.mapInstance?.panTo(this.center);
  }
}
