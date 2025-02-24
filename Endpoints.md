# User Authentication Endpoints
Handles user registration, login, token refresh, and password management.

Endpoint                        Method	    Description
/api/auth/register	            POST	    Register a new user.
/api/auth/login	                POST	    User login, returns JWT token.
/api/auth/refresh-token	        POST	    Refresh JWT access token.
/api/auth/logout	            POST	    Logout and invalidate tokens.
/api/auth/reset-password	    POST	    Request password reset link.
/api/auth/change-password	    POST	    Change password (authenticated).

# 2. User Profile Endpoints
Manage user information and preferences.

Endpoint	                    Method	    Description
/api/users/me	                GET	        Get the authenticated user’s profile.
/api/users/me	                PUT	        Update user profile.
/api/users/me/preferences	    PUT	        Update user preferences (e.g., default list view).

# 3. Shopping List Endpoints
Create, read, update, and delete shopping lists.

Endpoint	                    Method	    Description
/api/lists	                    GET	        Get all shopping lists for the user.
/api/lists	                    POST	    Create a new shopping list.
/api/lists/{listId}	            GET	        Get a specific shopping list.
/api/lists/{listId}	            PUT	        Update a shopping list’s name or properties.
/api/lists/{listId}	            DELETE	    Delete a shopping list.
/api/lists/{listId}/archive	    PUT	        Archive a shopping list.
/api/lists/{listId}/restore	    PUT	        Restore an archived list.

# 4. Item Management Endpoints
Manage items within a shopping list.

Endpoint	                                Method	    Description
/api/lists/{listId}/items	                GET	        Get all items in a list.
/api/lists/{listId}/items	                POST	    Add a new item to the list.
/api/lists/{listId}/items/{itemId}	        PUT	        Update an item (e.g., name, quantity).
/api/lists/{listId}/items/{itemId}	        DELETE	    Remove an item from the list.
/api/lists/{listId}/items/{itemId}/toggle	PUT	        Mark an item as purchased/unpurchased.

# 5. Sharing & Collaboration Endpoints (Optional but useful)
Allow users to share shopping lists with others.

Endpoint	                                Method	    Description
/api/lists/{listId}/share	                POST	    Share a list with another user (by email or user ID).
/api/lists/{listId}/collaborators	        GET	        Get list of collaborators.
/api/lists/{listId}/collaborators/{userId}	DELETE	    Remove a collaborator from the list.

# 6. Optional Features
## Reminders & Notifications:

Endpoint	                        Method	    Description
/api/notifications	                GET	        Get user notifications.
/api/lists/{listId}/reminders	    POST	    Set a reminder for a list.

## Categories & Favorites:
Endpoint	                        Method	        Description
/api/items/categories	            GET	            Get available item categories.
/api/users/me/favorites	            GET	            Get user’s favorite items.
/api/users/me/favorites/{itemId}	POST/DELETE	    Add or remove item from favorites.
