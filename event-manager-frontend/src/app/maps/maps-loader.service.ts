import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class MapsLoaderService {
  private loadingPromise: Promise<void> | null = null;

  load(): Promise<void> {
    if (this.isLoaded()) {
      return Promise.resolve();
    }

    if (this.loadingPromise) {
      return this.loadingPromise;
    }

    const apiKey = this.readApiKey();

    if (!apiKey) {
      return Promise.reject(
        new Error('Google Maps API key is missing. Add a meta tag named google-maps-api-key.')
      );
    }

    this.loadingPromise = new Promise((resolve, reject) => {
      const script = document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${apiKey}&libraries=places`;
      script.async = true;
      script.defer = true;
      script.onload = () => resolve();
      script.onerror = () => reject(new Error('Unable to load Google Maps.'));

      document.head.appendChild(script);
    });

    return this.loadingPromise;
  }

  private readApiKey(): string | null {
    const meta = document.querySelector<HTMLMetaElement>('meta[name="google-maps-api-key"]');
    return meta?.content?.trim() || null;
  }

  private isLoaded(): boolean {
    return typeof google !== 'undefined' && !!google.maps;
  }
}
